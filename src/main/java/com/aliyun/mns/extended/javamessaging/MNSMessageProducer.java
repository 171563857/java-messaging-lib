//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.extended.javamessaging.message.MNSBytesMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSJsonableMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSObjectMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSTextMessage;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Destination;
import javax.jms.IllegalStateException;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSender;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSMessageProducer implements MessageProducer, QueueSender {
    private static final Log LOG = LogFactory.getLog(MNSMessageProducer.class);
    private MNSQueueSession queueSession;
    private MNSQueueDestination queueDestination;
    private MNSQueueWrapper mnsQueueWrapper;
    final AtomicBoolean closed = new AtomicBoolean(false);

    public MNSMessageProducer(MNSClientWrapper clientWrapper, MNSQueueSession session, MNSQueueDestination queueDestination) throws JMSException {
        this.queueSession = session;
        this.queueDestination = queueDestination;
        this.mnsQueueWrapper = clientWrapper.generateMNSQueueWrapper(this.queueDestination.getQueueName());
    }

    void sendMessage2MNS(Queue queue, Message message) throws JMSException {
        if (this.closed.get()) {
            throw new IllegalStateException("The producer is closed.");
        } else {
            String mnsMessageBody = null;
            String messageType = null;
            if (!(message instanceof MNSMessage)) {
                throw new MessageFormatException("Unrecognized message type. Messages have to be one of: MNSBytesMessage, MNSObjectMessage, or MNSTextMessage");
            } else {
                message.setJMSDestination(queue);
                if (message instanceof MNSTextMessage) {
                    mnsMessageBody = ((MNSTextMessage)message).getText();
                    messageType = "text";
                } else if (message instanceof MNSBytesMessage) {
                    mnsMessageBody = Base64.encodeBase64String(((MNSBytesMessage)message).getBodyAsBytes());
                    messageType = "byte";
                } else if (message instanceof MNSObjectMessage) {
                    mnsMessageBody = ((MNSObjectMessage)message).getSerializedBody();
                    messageType = "object";
                }

                if (mnsMessageBody == null) {
                    throw new JMSException("Message body cannot be null!");
                } else {
                    MNSJsonableMessage jsonableMessage = new MNSJsonableMessage();
                    Enumeration propertyNames = message.getPropertyNames();

                    String jsonMessage;
                    String messageId;
                    while(propertyNames.hasMoreElements()) {
                        jsonMessage = (String)propertyNames.nextElement();
                        JMSMessagePropertyValue propertyObject = ((MNSMessage)message).getJMSMessagePropertyValue(jsonMessage);
                        messageId = propertyObject.getType();
                        String propertyValue = propertyObject.getStringMessageAttributeValue();
                        jsonableMessage.addProperty(jsonMessage, messageId, propertyValue);
                    }

                    jsonableMessage.addProperty("JMS_MNSMessageType", "String", messageType);
                    jsonableMessage.setMessageBody(mnsMessageBody);
                    jsonMessage = JSON.toJSONString(jsonableMessage);
                    com.aliyun.mns.model.Message mnsMessage = new com.aliyun.mns.model.Message(jsonMessage);
                    messageId = this.mnsQueueWrapper.sendMessage(mnsMessage).getMessageId();
                    LOG.info("Message sent to MNS with MNS-assigned messageId: " + messageId);
                    message.setJMSMessageID(String.format("ID:%s", messageId));
                }
            }
        }
    }

    public void send(Queue queue, Message message) throws JMSException {
        if (!(queue instanceof MNSQueueDestination)) {
            throw new InvalidDestinationException("Incompatible implementation! Only support MNSQueueDestination.");
        } else if (this.queueDestination != null) {
            throw new UnsupportedOperationException("MessageProducer already specified a destination when constructed.");
        } else {
            this.sendMessage2MNS(queue, message);
        }
    }

    public void send(Message message) throws JMSException {
        if (this.queueDestination == null) {
            throw new UnsupportedOperationException("MessageProducer should specified a destination when constructed.");
        } else {
            this.sendMessage2MNS(this.queueDestination, message);
        }
    }

    public void send(Destination destination, Message message) throws JMSException {
        if (destination != null && destination instanceof MNSQueueDestination) {
            if (this.queueDestination != null) {
                throw new UnsupportedOperationException("MessageProducer already specified a destination when constructed.");
            } else {
                this.sendMessage2MNS((Queue)destination, message);
            }
        } else {
            throw new InvalidDestinationException("Destination must be an instance of MNSQueueDestination ");
        }
    }

    public Queue getQueue() throws JMSException {
        return this.queueDestination;
    }

    public Destination getDestination() throws JMSException {
        return this.queueDestination;
    }

    public void close() throws JMSException {
        if (this.closed.compareAndSet(false, true)) {
            this.queueSession.removeProducer(this);
        }

    }

    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setDisableMessageID(boolean value) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public boolean getDisableMessageID() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public int getDeliveryMode() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setPriority(int defaultPriority) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public int getPriority() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setTimeToLive(long timeToLive) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public long getTimeToLive() throws JMSException {
        throw new JMSException("Unsupported Method");
    }
}
