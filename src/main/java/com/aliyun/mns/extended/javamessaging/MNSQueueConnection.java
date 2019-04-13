//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.extended.javamessaging.acknowledge.AcknowledgeMode;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSQueueConnection implements Connection, QueueConnection {
    private static final Log LOG = LogFactory.getLog(MNSQueueConnection.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap());
    private String clientID;
    private final MNSClientWrapper mnsClientWrapper;

    public MNSQueueConnection(MNSClientWrapper clientWrapper) {
        this.mnsClientWrapper = clientWrapper;
    }

    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return (QueueSession)this.createSession(transacted, acknowledgeMode);
    }

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        this.checkClosed();
        if (!transacted && acknowledgeMode != 0) {
            if (acknowledgeMode == 1) {
                MNSQueueSession session = new MNSQueueSession(this, AcknowledgeMode.ACK_AUTO.withOriginalAcknowledgeMode(acknowledgeMode));
                this.sessions.add(session);
                AtomicBoolean var4 = this.running;
                synchronized(this.running) {
                    if (this.running.get()) {
                        session.start();
                    }

                    return session;
                }
            } else {
                LOG.error("Unrecognized acknowledgeMode. Cannot create Session.");
                throw new JMSException("Unrecognized acknowledgeMode. Cannot create Session.");
            }
        } else {
            throw new JMSException("Unsupported Method");
        }
    }

    public MNSClientWrapper getMNSClientWrapper() {
        return this.mnsClientWrapper;
    }

    public String getClientID() throws JMSException {
        return null;
    }

    public void setClientID(String clientID) throws JMSException {
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return null;
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return null;
    }

    public void setExceptionListener(ExceptionListener listener) throws JMSException {
    }

    public void start() throws JMSException {
        this.checkClosed();
        if (!this.running.get()) {
            AtomicBoolean var1 = this.running;
            synchronized(this.running) {
                if (!this.running.get()) {
                    Iterator i$ = this.sessions.iterator();

                    while(i$.hasNext()) {
                        Session session = (Session)i$.next();
                        MNSQueueSession queueSession = (MNSQueueSession)session;
                        queueSession.start();
                    }

                    this.running.set(true);
                }

            }
        }
    }

    public void stop() throws JMSException {
        this.checkClosed();
        if (this.running.get()) {
            if (MNSQueueSession.CALLBACK_SCHEDULER_THREAD_FACTORY.wasThreadCreatedWithThisThreadGroup(Thread.currentThread())) {
                throw new IllegalStateException("MessageListener must not attempt to stop its own Connection to prevent potential deadlock issues");
            } else {
                AtomicBoolean var1 = this.running;
                synchronized(this.running) {
                    if (this.running.get()) {
                        Iterator i$ = this.sessions.iterator();

                        while(i$.hasNext()) {
                            Session session = (Session)i$.next();
                            MNSQueueSession queueSession = (MNSQueueSession)session;
                            queueSession.stop();
                        }

                        this.running.set(false);
                    }

                }
            }
        }
    }

    public void close() throws JMSException {
        AtomicBoolean var1 = this.closed;
        synchronized(this.closed) {
            if (!this.closed.get()) {
                this.stop();
                if (MNSQueueSession.CALLBACK_SCHEDULER_THREAD_FACTORY.wasThreadCreatedWithThisThreadGroup(Thread.currentThread())) {
                    throw new IllegalStateException("MessageListener must not attempt to close its own Connection to prevent potential deadlock issues");
                }

                Iterator i$ = this.sessions.iterator();

                while(i$.hasNext()) {
                    Session session = (Session)i$.next();
                    MNSQueueSession queueSession = (MNSQueueSession)session;
                    queueSession.close();
                }

                this.sessions.clear();
                this.closed.set(true);
                this.mnsClientWrapper.destory();
            }

        }
    }

    public void removeSession(MNSQueueSession session) {
        this.sessions.remove(session);
    }

    public void checkClosed() throws IllegalStateException {
        if (this.closed.get()) {
            throw new IllegalStateException("Connection is closd");
        }
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new JMSException("Unsupported Method");
    }
}
