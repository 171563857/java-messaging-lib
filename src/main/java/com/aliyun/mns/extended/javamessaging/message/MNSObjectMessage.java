//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSObjectMessage extends MNSMessage implements ObjectMessage {
    private static final Log LOG = LogFactory.getLog(MNSObjectMessage.class);
    private String serializedBody;

    public MNSObjectMessage(String messageBody, Map<String, JMSMessagePropertyValue> jmsProperties, Acknowledger acknowledger, String queueURL, String receiptHandle) {
        super(acknowledger, jmsProperties, queueURL, receiptHandle);
        this.serializedBody = messageBody;
    }

    public MNSObjectMessage() {
    }

    public MNSObjectMessage(Serializable object) throws JMSException {
        this.serializedBody = serialize(object);
    }

    public void setObject(Serializable object) throws JMSException {
        this.checkBodyWritePermissions();
        this.setSerializedBody(serialize(object));
    }

    public Serializable getObject() throws JMSException {
        return deserialize(this.getSerializedBody());
    }

    public void internalClearBody() throws JMSException {
        this.setSerializedBody((String)null);
    }

    protected static Serializable deserialize(String serialized) throws JMSException {
        if (serialized == null) {
            return null;
        } else {
            ObjectInputStream objectInputStream = null;

            Serializable deserializedObject;
            try {
                byte[] bytes = Base64.decodeBase64(serialized);
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                deserializedObject = (Serializable)objectInputStream.readObject();
            } catch (IOException var12) {
                LOG.error("IOException: Message cannot be written", var12);
                throw convertExceptionToMessageFormatException(var12);
            } catch (Exception var13) {
                LOG.error("Unexpected exception: ", var13);
                throw convertExceptionToMessageFormatException(var13);
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException var11) {
                        LOG.warn(var11.getMessage());
                    }
                }

            }

            return deserializedObject;
        }
    }

    protected static String serialize(Serializable serializable) throws JMSException {
        if (serializable == null) {
            return null;
        } else {
            ObjectOutputStream objectOutputStream = null;

            String serializedString;
            try {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                objectOutputStream = new ObjectOutputStream(bytesOut);
                objectOutputStream.writeObject(serializable);
                objectOutputStream.flush();
                serializedString = Base64.encodeBase64String(bytesOut.toByteArray());
            } catch (IOException var11) {
                LOG.error("IOException: cannot serialize objectMessage", var11);
                throw convertExceptionToMessageFormatException(var11);
            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (IOException var10) {
                        LOG.warn(var10.getMessage());
                    }
                }

            }

            return serializedString;
        }
    }

    protected static MessageFormatException convertExceptionToMessageFormatException(Exception e) {
        MessageFormatException ex = new MessageFormatException(e.getMessage());
        ex.initCause(e);
        return ex;
    }

    public String getSerializedBody() {
        return this.serializedBody;
    }

    public void setSerializedBody(String serializedBody) {
        this.serializedBody = serializedBody;
    }
}
