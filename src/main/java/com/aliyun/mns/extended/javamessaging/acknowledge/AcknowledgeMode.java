//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.acknowledge;

import com.aliyun.mns.extended.javamessaging.MNSClientWrapper;
import com.aliyun.mns.extended.javamessaging.MNSQueueSession;
import javax.jms.JMSException;

public enum AcknowledgeMode {
    ACK_AUTO,
    ACK_UNORDERED,
    ACK_RANGE;

    private int originalAcknowledgeMode;

    private AcknowledgeMode() {
    }

    public AcknowledgeMode withOriginalAcknowledgeMode(int originalAcknowledgeMode) {
        this.originalAcknowledgeMode = originalAcknowledgeMode;
        return this;
    }

    public int getOriginalAcknowledgeMode() {
        return this.originalAcknowledgeMode;
    }

    public Acknowledger createAcknowledger(MNSClientWrapper client, MNSQueueSession parentSession) throws JMSException {
        switch(this) {
            case ACK_AUTO:
                return new AutoAcknowledger(client, parentSession);
            default:
                throw new JMSException(this + " - AcknowledgeMode does not exist");
        }
    }
}
