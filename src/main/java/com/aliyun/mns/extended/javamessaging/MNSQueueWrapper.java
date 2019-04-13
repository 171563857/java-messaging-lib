//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import java.util.List;
import javax.jms.JMSException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSQueueWrapper {
    private static final Log LOG = LogFactory.getLog(MNSQueueWrapper.class);
    private CloudQueue queue = null;

    public MNSQueueWrapper(CloudQueue queue) {
        this.queue = queue;
    }

    public Message sendMessage(Message message) throws JMSException {
        try {
            return this.queue.putMessage(message);
        } catch (Exception var3) {
            throw this.handleException(var3);
        }
    }

    private JMSException handleException(Exception e) {
        JMSException jmsException;
        if (e instanceof ClientException) {
            ClientException exception = (ClientException)e;
            LOG.error(exception);
            jmsException = new JMSException(exception.getMessage(), exception.getErrorCode());
        } else if (e instanceof ServiceException) {
            ServiceException exception = (ServiceException)e;
            LOG.error(exception);
            jmsException = new JMSException(exception.getMessage(), exception.getErrorCode());
        } else {
            LOG.error(e);
            jmsException = new JMSException(e.getMessage());
        }

        jmsException.initCause(e);
        return jmsException;
    }

    public void changeMessageVisibilityTimeout(String receiptHandle, int visibilityTimeout) throws JMSException {
        try {
            this.queue.changeMessageVisibilityTimeout(receiptHandle, visibilityTimeout);
        } catch (Exception var4) {
            if (!(var4 instanceof ServiceException) || !this.queue.isMessageNotExist((ServiceException)var4)) {
                throw this.handleException(var4);
            }

            LOG.warn("MessageNotExist for gaven ReceiptHandle: " + receiptHandle);
        }

    }

    public void deleteMessage(String receiptHandle) throws JMSException {
        try {
            this.queue.deleteMessage(receiptHandle);
        } catch (Exception var3) {
            if (!(var3 instanceof ServiceException) || !this.queue.isMessageNotExist((ServiceException)var3)) {
                throw this.handleException(var3);
            }

            LOG.warn("MessageNotExist for gaven ReceiptHandle: " + receiptHandle);
        }

    }

    public List<Message> batchPopMessages(int prefetchBatchSize, int pollingWaitSeconds) throws JMSException {
        try {
            return this.queue.batchPopMessage(prefetchBatchSize, pollingWaitSeconds);
        } catch (Exception var4) {
            throw this.handleException(var4);
        }
    }

    public Message popMessage(int pollingWaitSeconds) throws JMSException {
        try {
            return this.queue.popMessage(pollingWaitSeconds);
        } catch (Exception var3) {
            throw this.handleException(var3);
        }
    }

    public String getQueueURL() {
        return this.queue.getQueueURL();
    }
}
