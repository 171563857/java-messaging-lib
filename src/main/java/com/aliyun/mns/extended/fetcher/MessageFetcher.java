package com.aliyun.mns.extended.fetcher;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.extended.javamessaging.MNSMessageConsumer;
import com.aliyun.mns.extended.javamessaging.MNSQueueWrapper;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSBytesMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSJsonableMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSJsonableProperty;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSObjectMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSTextMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import com.aliyun.mns.extended.util.ExponentialBackoffStrategy;
import com.aliyun.mns.model.Message;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageFetcher implements Runnable, MessageFetcherManager {
    private static final Log LOG = LogFactory.getLog(MessageFetcher.class);
    private static final int POLLING_WAIT_SECONDS = 30;
    private MNSMessageConsumer consumer;
    private MNSQueueWrapper queue;
    private MessageListener listener = null;
    private Acknowledger acknowledger;
    private int ackMode;
    protected int messagesPrefetched = 0;
    protected int retriesAttempted = 0;
    protected ExponentialBackoffStrategy backoffStrategy = new ExponentialBackoffStrategy(25L, 25L, 2000L);
    protected volatile boolean closed = false;
    protected volatile boolean running = false;
    private final Object stateLock = new Object();

    public MessageFetcher(MNSQueueWrapper queue, Acknowledger acknowledger, int ackMode) {
        this.queue = queue;
        this.acknowledger = acknowledger;
        this.ackMode = ackMode;
    }

    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
        if (listener != null && !this.isClosed()) {
            Object var2 = this.stateLock;
            synchronized(this.stateLock) {
                if (this.running && !this.isClosed()) {
                    this.notifyStateChange();
                }
            }
        }
    }

    public MessageListener getMessageListener() {
        return this.listener;
    }

    public void run() {
        while(true) {
            Message message = null;

            try {
                if (!this.isClosed()) {
                    Object var2 = this.stateLock;
                    synchronized(this.stateLock) {
                        this.waitForStart();
                        this.waitForListener();
                    }

                    if (!this.isClosed()) {
                        message = this.getMessage();
                    }

                    if (message != null) {
                        this.processReceivedMessage(message);
                    }
                    continue;
                }
            } catch (InterruptedException var5) {
                ;
            } catch (Throwable var6) {
                LOG.error("Unexpected exception when fetch messages:", var6);
                continue;
            }

            return;
        }
    }

    protected Message getMessage() throws InterruptedException {
        Message message = null;

        try {
            message = this.queue.popMessage(30);
            if (message == null) {
                LOG.debug("messages null");
            }

            this.retriesAttempted = 0;
        } catch (Exception var5) {
            LOG.warn("Encountered exception during receive in ConsumerPrefetch thread", var5);

            try {
                this.sleep(this.backoffStrategy.delayBeforeNextRetry(this.retriesAttempted++));
            } catch (InterruptedException var4) {
                LOG.warn("Interrupted while retrying on receive", var4);
                throw var4;
            }
        }

        return message;
    }

    protected javax.jms.Message convertToJMSMessage(Message message) throws JMSException {
        String messageBody = message.getMessageBodyAsString();
        LOG.info("xxxdata: " + messageBody);
        MNSJsonableMessage jsonableMessage = (MNSJsonableMessage)JSON.parseObject(messageBody, MNSJsonableMessage.class);
        List<MNSJsonableProperty> properties = jsonableMessage.getProperties();
        Map<String, JMSMessagePropertyValue> jmsProperties = new HashMap();
        String messageType = "text";
        Iterator jmsMessageIterator = properties.iterator();

        while(jmsMessageIterator.hasNext()) {
            MNSJsonableProperty property = (MNSJsonableProperty)jmsMessageIterator.next();
            if ("JMS_MNSMessageType".equals(property.getPropertyName())) {
                messageType = property.getPropertyValue();
            } else {
                JMSMessagePropertyValue jmsMessagePropertyValue = new JMSMessagePropertyValue(property.getPropertyValue(), property.getPropertyType());
                jmsProperties.put(property.getPropertyName(), jmsMessagePropertyValue);
            }
        }
        jmsMessageIterator = null;
        Object jmsMessage;
        if ("byte".equals(messageType)) {
            try {
                jmsMessage = new MNSBytesMessage(jsonableMessage.getMessageBody(), jmsProperties, this.acknowledger, this.queue.getQueueURL(), message.getReceiptHandle());
            } catch (JMSException var10) {
                LOG.warn("MessageReceiptHandle - " + message.getReceiptHandle() + "cannot be serialized to BytesMessage", var10);
                throw var10;
            }
        } else if ("object".equals(messageType)) {
            jmsMessage = new MNSObjectMessage(jsonableMessage.getMessageBody(), jmsProperties, this.acknowledger, this.queue.getQueueURL(), message.getReceiptHandle());
        } else {
            if (!"text".equals(messageType)) {
                throw new JMSException("Not a supported JMS message type");
            }

            jmsMessage = new MNSTextMessage(jsonableMessage.getMessageBody(), jmsProperties, this.acknowledger, this.queue.getQueueURL(), message.getReceiptHandle());
        }

        return (javax.jms.Message)jmsMessage;
    }

    protected void processReceivedMessage(Message message) throws JMSException {
        if (message != null) {
            MessageListener listener2 = this.listener;
            if (listener2 != null) {
                try {
                    javax.jms.Message jmsMessage = this.convertToJMSMessage(message);
                    boolean callbackFailed = false;
                    boolean var14 = false;

                    label115: {
                        MNSMessage mnsMessage;
                        label114: {
                            try {
                                var14 = true;
                                listener2.onMessage(jmsMessage);
                                var14 = false;
                                break label114;
                            } catch (Throwable var17) {
                                LOG.warn("Exception thrown from onMessage callback for message", var17);
                                callbackFailed = true;
                                var14 = false;
                            } finally {
                                if (var14) {
                                    if (!callbackFailed) {
                                        mnsMessage = (MNSMessage)jmsMessage;
                                        if (this.ackMode == 1) {
                                            mnsMessage.acknowledge();
                                        }
                                    }

                                }
                            }

                            if (!callbackFailed) {
                                mnsMessage = (MNSMessage)jmsMessage;
                                if (this.ackMode == 1) {
                                    mnsMessage.acknowledge();
                                }
                            }
                            break label115;
                        }

                        if (!callbackFailed) {
                            mnsMessage = (MNSMessage)jmsMessage;
                            if (this.ackMode == 1) {
                                mnsMessage.acknowledge();
                            }
                        }
                    }

                    Object var20 = this.stateLock;
                    synchronized(this.stateLock) {
                        this.notifyStateChange();
                    }
                } catch (JMSException var19) {
                    LOG.warn("processReceivedMessages Exception: " + message.getMessageId() + " " + var19.toString());
                }
            } else {
                try {
                    this.queue.changeMessageVisibilityTimeout(message.getReceiptHandle(), 1);
                } catch (Exception var15) {
                    LOG.warn("changeMessageVisibilityTimeout fail: " + message.getReceiptHandle() + " " + var15.toString());
                }
            }

        }
    }

    protected void waitForStart() throws InterruptedException {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            while(!this.running && !this.isClosed()) {
                try {
                    LOG.info("wait for start");
                    this.stateLock.wait();
                    LOG.info("wakeup try to check listener");
                } catch (InterruptedException var4) {
                    LOG.warn("Interrupted while waiting on consumer start", var4);
                    throw var4;
                }
            }

        }
    }

    protected void waitForListener() throws InterruptedException {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            while(this.listener == null && !this.isClosed()) {
                try {
                    LOG.info("wait for listener");
                    this.stateLock.wait();
                    LOG.info("wakeup try to fetch");
                } catch (InterruptedException var4) {
                    LOG.warn("Interrupted while waiting on listener", var4);
                    throw var4;
                }
            }

        }
    }

    protected void notifyStateChange() {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            this.stateLock.notifyAll();
        }
    }

    public void start() {
        if (!this.isClosed() && !this.running) {
            Object var1 = this.stateLock;
            synchronized(this.stateLock) {
                this.running = true;
                this.notifyStateChange();
            }
        }
    }

    public void stop() {
        if (!this.isClosed() && this.running) {
            Object var1 = this.stateLock;
            synchronized(this.stateLock) {
                this.running = false;
                this.notifyStateChange();
            }
        }
    }

    public void close() {
        if (!this.isClosed()) {
            Object var1 = this.stateLock;
            synchronized(this.stateLock) {
                this.closed = true;
                this.notifyStateChange();
                this.listener = null;
            }
        }
    }

    protected boolean isClosed() {
        return this.closed;
    }

    public void messageDispatched() {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            this.notifyStateChange();
        }
    }

    public MNSMessageConsumer getConsumer() {
        return this.consumer;
    }

    public void setMessageConsumer(MNSMessageConsumer consumer) {
        this.consumer = consumer;
    }

    protected void sleep(long sleepTimeMillis) throws InterruptedException {
        try {
            Thread.sleep(sleepTimeMillis);
        } catch (InterruptedException var4) {
            throw var4;
        }
    }

    public MNSQueueWrapper getQueue() {
        return this.queue;
    }

    public javax.jms.Message receive() throws Exception {
        throw new JMSException("Unsupported Method");
    }

    public javax.jms.Message receive(long timeout) throws Exception {
        throw new JMSException("Unsupported Method");
    }

    public static class MessageManager {
        private final MessageFetcherManager prefetchManager;
        private final javax.jms.Message message;

        public MessageManager(MessageFetcherManager prefetchManager, javax.jms.Message message) {
            this.prefetchManager = prefetchManager;
            this.message = message;
        }

        public MessageFetcherManager getPrefetchManager() {
            return this.prefetchManager;
        }

        public javax.jms.Message getMessage() {
            return this.message;
        }
    }
}
