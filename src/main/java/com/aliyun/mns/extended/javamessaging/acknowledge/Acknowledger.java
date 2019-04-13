//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.acknowledge;

import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import javax.jms.JMSException;

public interface Acknowledger {
    void acknowledge(MNSMessage var1) throws JMSException;

    void notifyMessageReceived(MNSMessage var1) throws JMSException;
}
