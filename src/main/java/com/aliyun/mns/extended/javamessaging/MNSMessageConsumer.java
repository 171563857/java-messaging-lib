//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.fetcher.MessageFetcher;
import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.util.ThreadFactoryHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSMessageConsumer implements MessageConsumer, QueueReceiver {
    private static final Log LOG = LogFactory.getLog(MNSMessageConsumer.class);
    public static final int PREFETCH_EXECUTOR_GRACEFUL_SHUTDOWN_TIME = 32;
    private MNSQueueConnection parentConnection;
    private MNSQueueSession parentSession;
    private MNSQueueDestination destination;
    private MNSQueueWrapper mnsQueueWrapper;
    protected volatile boolean closed = false;
    private Acknowledger acknowledger;
    private final ExecutorService prefetchExecutor;
    private final MessageFetcher messagePrefetcher;
    private final ThreadFactoryHelper prefetchThreadHelper;

    public MNSMessageConsumer(MNSQueueConnection connection, MNSQueueSession session, Acknowledger acknowledger, MNSClientWrapper mnsClientWrapper, MNSQueueDestination destination, ThreadFactoryHelper prefetchThreadHelper) throws JMSException {
        this.parentConnection = connection;
        this.parentSession = session;
        this.prefetchThreadHelper = prefetchThreadHelper;
        this.acknowledger = acknowledger;
        this.mnsQueueWrapper = mnsClientWrapper.generateMNSQueueWrapper(destination.getQueueName());
        this.messagePrefetcher = new MessageFetcher(this.mnsQueueWrapper, this.acknowledger, this.parentSession.getAcknowledgeMode());
        this.messagePrefetcher.setMessageConsumer(this);
        this.prefetchExecutor = Executors.newSingleThreadExecutor(this.prefetchThreadHelper);
        this.prefetchExecutor.execute(this.messagePrefetcher);
    }

    public Queue getQueue() throws JMSException {
        return this.destination;
    }

    public MessageListener getMessageListener() throws JMSException {
        return this.messagePrefetcher.getMessageListener();
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        this.messagePrefetcher.setMessageListener(listener);
    }

    public void close() throws JMSException {
        if (!this.closed) {
            this.doClose();
        }
    }

    void doClose() {
        if (!this.closed) {
            this.messagePrefetcher.close();
            this.parentSession.removeConsumer(this);

            try {
                if (!this.prefetchExecutor.isShutdown()) {
                    LOG.info("Shutting down fetcher executor");
                    this.prefetchExecutor.shutdown();
                }

                this.parentSession.waitForConsumerCallbackToComplete(this);
                if (!this.prefetchExecutor.awaitTermination(32L, TimeUnit.SECONDS)) {
                    LOG.warn("Can't terminate executor service perfetcher after 32 seconds, some running threads will be shutdown immediately");
                    this.prefetchExecutor.shutdownNow();
                }
            } catch (InterruptedException var2) {
                LOG.error("Interrupted while closing the consumer.", var2);
            }

            this.closed = true;
        }
    }

    void recover() throws JMSException {
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void startPrefetch() {
        this.messagePrefetcher.start();
    }

    public void stopPrefetch() {
        this.messagePrefetcher.stop();
    }

    public String getMessageSelector() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public Message receive() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public Message receive(long timeout) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public Message receiveNoWait() throws JMSException {
        throw new JMSException("Unsupported Method");
    }
}
