//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.javamessaging.acknowledge.AcknowledgeMode;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSBytesMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSObjectMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSTextMessage;
import com.aliyun.mns.extended.util.ThreadFactoryHelper;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSQueueSession implements Session, QueueSession {
    private final Log LOG;
    private final AtomicBoolean closed;
    private final AtomicBoolean running;
    private final AtomicBoolean closing;
    private final MNSClientWrapper mnsClientWrapper;
    private final MNSQueueConnection parentConnection;
    private final AcknowledgeMode acknowledgeMode;
    private final Acknowledger acknowledger;
    private final Set<MNSMessageProducer> messageProducers;
    private final Set<MNSMessageConsumer> messageConsumers;
    static final ThreadFactoryHelper CALLBACK_SCHEDULER_THREAD_FACTORY = new ThreadFactoryHelper("CallbackSchedulers", true);
    static final ThreadFactoryHelper CONSUMER_PREFETCH_THREAD_FACTORY = new ThreadFactoryHelper("Prefetchers", true);
    private Thread activeCallbackSessionThread;
    private MNSMessageConsumer activeConsumerInCallback;
    private final Object stateLock;

    public MNSQueueSession(MNSQueueConnection parentConnection, AcknowledgeMode acknowledgeMode) throws JMSException {
        this(parentConnection, acknowledgeMode, Collections.newSetFromMap(new ConcurrentHashMap()), Collections.newSetFromMap(new ConcurrentHashMap()));
    }

    public MNSQueueSession(MNSQueueConnection parentConnection, AcknowledgeMode acknowledgeMode, Set<MNSMessageProducer> producers, Set<MNSMessageConsumer> consumers) throws JMSException {
        this.LOG = LogFactory.getLog(MNSQueueSession.class);
        this.closed = new AtomicBoolean(false);
        this.running = new AtomicBoolean(false);
        this.closing = new AtomicBoolean(false);
        this.activeConsumerInCallback = null;
        this.stateLock = new Object();
        this.parentConnection = parentConnection;
        this.mnsClientWrapper = parentConnection.getMNSClientWrapper();
        this.acknowledgeMode = acknowledgeMode;
        this.acknowledger = this.acknowledgeMode.createAcknowledger(this.mnsClientWrapper, this);
        this.messageProducers = producers;
        this.messageConsumers = consumers;
    }

    public boolean getTransacted() throws JMSException {
        return false;
    }

    public int getAcknowledgeMode() throws JMSException {
        return this.acknowledgeMode.getOriginalAcknowledgeMode();
    }

    public void start() throws IllegalStateException {
        this.checkClosed();
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            this.checkClosing();
            this.running.set(true);
            Iterator i$ = this.messageConsumers.iterator();

            while(i$.hasNext()) {
                MNSMessageConsumer consumer = (MNSMessageConsumer)i$.next();
                consumer.startPrefetch();
            }

            this.stateLock.notifyAll();
        }
    }

    public void stop() throws IllegalStateException {
        this.checkClosed();
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            this.checkClosing();
            this.running.set(false);
            Iterator i$ = this.messageConsumers.iterator();

            while(i$.hasNext()) {
                MNSMessageConsumer consumer = (MNSMessageConsumer)i$.next();
                consumer.stopPrefetch();
            }

            this.waitForCallbackComplete();
            this.stateLock.notifyAll();
        }
    }

    public void checkClosed() throws IllegalStateException {
        if (this.closed.get()) {
            throw new IllegalStateException("Session is closed");
        }
    }

    public void checkClosing() throws IllegalStateException {
        if (this.closing.get()) {
            throw new IllegalStateException("Session is closing");
        }
    }

    public synchronized void close() throws JMSException {
        if (!this.closed.get()) {
            this.closing.set(true);
            if (this.isActiveCallbackSessionThread()) {
                throw new IllegalStateException("MessageListener must not attempt to close its own Session to prevent potential deadlock issues");
            }

            try {
                this.parentConnection.removeSession(this);
                Iterator i$ = this.messageProducers.iterator();

                while(i$.hasNext()) {
                    MNSMessageProducer producer = (MNSMessageProducer)i$.next();
                    producer.close();
                }

                i$ = this.messageConsumers.iterator();

                while(i$.hasNext()) {
                    MNSMessageConsumer consumer = (MNSMessageConsumer)i$.next();
                    consumer.close();
                    consumer.recover();
                }
            } finally {
                this.closed.set(true);
            }
        }

    }

    public void recover() throws JMSException {
        Iterator i$ = this.messageConsumers.iterator();

        while(i$.hasNext()) {
            MNSMessageConsumer consumer = (MNSMessageConsumer)i$.next();
            consumer.recover();
        }

    }

    public void startingCallback(MNSMessageConsumer consumer) throws InterruptedException, Exception {
        if (!this.closed.get()) {
            Object var2 = this.stateLock;
            synchronized(this.stateLock) {
                if (this.activeConsumerInCallback != null) {
                    throw new IllegalStateException("Callback already in progress");
                } else {
                    assert this.activeCallbackSessionThread == null;

                    while(!this.running.get() && !this.closing.get()) {
                        try {
                            this.stateLock.wait();
                        } catch (InterruptedException var5) {
                            this.LOG.warn("Interrupted while waiting on session start. Continue to wait...", var5);
                        }
                    }

                    this.checkClosing();
                    this.activeConsumerInCallback = consumer;
                    this.activeCallbackSessionThread = Thread.currentThread();
                }
            }
        }
    }

    public void finishedCallback() throws Exception {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            if (this.activeConsumerInCallback == null) {
                throw new IllegalStateException("Callback not in progress");
            } else {
                this.activeConsumerInCallback = null;
                this.activeCallbackSessionThread = null;
                this.stateLock.notifyAll();
            }
        }
    }

    public boolean isActiveCallbackSessionThread() {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            return this.activeCallbackSessionThread == Thread.currentThread();
        }
    }

    public void waitForConsumerCallbackToComplete(MessageConsumer consumer) throws InterruptedException {
        Object var2 = this.stateLock;
        synchronized(this.stateLock) {
            while(this.activeConsumerInCallback == consumer) {
                try {
                    this.stateLock.wait();
                } catch (InterruptedException var5) {
                    this.LOG.warn("Interrupted while waiting the active consumer in callback to complete. Continue to wait...", var5);
                }
            }

        }
    }

    void waitForCallbackComplete() {
        Object var1 = this.stateLock;
        synchronized(this.stateLock) {
            while(this.activeConsumerInCallback != null) {
                try {
                    this.stateLock.wait();
                } catch (InterruptedException var4) {
                    this.LOG.warn("Interrupted while waiting on session callback completion. Continue to wait...", var4);
                }
            }

        }
    }

    public void run() {
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        if (destination != null && destination instanceof MNSQueueDestination) {
            AtomicBoolean var2 = this.closed;
            synchronized(this.closed) {
                this.checkClosed();
                MNSMessageProducer producer = new MNSMessageProducer(this.mnsClientWrapper, this, (MNSQueueDestination)destination);
                this.messageProducers.add(producer);
                return producer;
            }
        } else {
            throw new JMSException("Actual type of destination must be MNSQueueDestination");
        }
    }

    void removeProducer(MNSMessageProducer producer) {
        this.messageProducers.remove(producer);
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        if (destination != null && destination instanceof MNSQueueDestination) {
            AtomicBoolean var2 = this.closed;
            synchronized(this.closed) {
                this.checkClosed();
                MNSMessageConsumer consumer = new MNSMessageConsumer(this.parentConnection, this, this.acknowledger, this.mnsClientWrapper, (MNSQueueDestination)destination, CONSUMER_PREFETCH_THREAD_FACTORY);
                this.messageConsumers.add(consumer);
                if (this.running.get()) {
                    consumer.startPrefetch();
                }

                return consumer;
            }
        } else {
            throw new JMSException("Actual type of destination must be MNSQueueDestination");
        }
    }

    void removeConsumer(MNSMessageConsumer consumer) {
        this.messageConsumers.remove(consumer);
    }

    public Queue createQueue(String queueName) throws JMSException {
        this.checkClosed();
        return new MNSQueueDestination(queueName);
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return (QueueReceiver)this.createConsumer(queue);
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        return (QueueSender)this.createProducer(queue);
    }

    public BytesMessage createBytesMessage() throws JMSException {
        this.checkClosed();
        return new MNSBytesMessage();
    }

    public Message createMessage() throws JMSException {
        this.checkClosed();
        return new MNSTextMessage();
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        this.checkClosed();
        return new MNSObjectMessage();
    }

    public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
        this.checkClosed();
        return new MNSObjectMessage(object);
    }

    public TextMessage createTextMessage() throws JMSException {
        this.checkClosed();
        return new MNSTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        this.checkClosed();
        return new MNSTextMessage(text);
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void commit() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void rollback() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public Topic createTopic(String topicName) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void unsubscribe(String name) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public MapMessage createMapMessage() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public StreamMessage createStreamMessage() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public MessageListener getMessageListener() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        throw new JMSException("Unsupported Method");
    }
}
