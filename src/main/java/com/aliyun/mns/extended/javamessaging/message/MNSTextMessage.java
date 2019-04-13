//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.TextMessage;

public class MNSTextMessage extends MNSMessage implements TextMessage {
    private String text;

    public MNSTextMessage() {
    }

    public MNSTextMessage(String text, Map<String, JMSMessagePropertyValue> properties, Acknowledger acknowledger, String queueURL, String receiptHandle) {
        super(acknowledger, properties, queueURL, receiptHandle);
        this.text = text;
    }

    public MNSTextMessage(String text) {
        this.text = text;
    }

    public void setText(String text) throws JMSException {
        this.checkBodyWritePermissions();
        this.text = text;
    }

    public String getText() throws JMSException {
        return this.text;
    }

    public void internalClearBody() throws JMSException {
        this.text = null;
    }
}
