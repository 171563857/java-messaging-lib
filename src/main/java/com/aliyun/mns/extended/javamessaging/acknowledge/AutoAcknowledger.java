//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.acknowledge;

import com.aliyun.mns.extended.javamessaging.MNSClientWrapper;
import com.aliyun.mns.extended.javamessaging.MNSQueueSession;
import com.aliyun.mns.extended.javamessaging.MNSQueueWrapper;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import javax.jms.JMSException;

public class AutoAcknowledger implements Acknowledger {
    private MNSClientWrapper clientWrapper;
    private MNSQueueSession session;

    public AutoAcknowledger(MNSClientWrapper clientWrapper, MNSQueueSession session) {
        this.clientWrapper = clientWrapper;
        this.session = session;
    }

    public void acknowledge(MNSMessage message) throws JMSException {
        this.session.checkClosed();
        MNSQueueWrapper queueWrapper = this.clientWrapper.generateMNSQueueWrapper(message.getQueueURL());
        queueWrapper.deleteMessage(message.getReceiptHandle());
    }

    public void notifyMessageReceived(MNSMessage message) throws JMSException {
        this.acknowledge(message);
    }
}
