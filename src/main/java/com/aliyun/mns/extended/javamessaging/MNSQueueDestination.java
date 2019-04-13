//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;

public class MNSQueueDestination implements Destination, Queue {
    private String queueName;
    private String endpoint;

    public MNSQueueDestination(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() throws JMSException {
        return this.queueName;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public int hashCode() {
        int result = 31 * 1 + (this.endpoint == null ? 0 : this.endpoint.hashCode());
        result = 31 * result + (this.queueName == null ? 0 : this.queueName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            MNSQueueDestination other = (MNSQueueDestination)obj;
            if (this.endpoint == null) {
                if (other.endpoint != null) {
                    return false;
                }
            } else if (!this.endpoint.equals(other.endpoint)) {
                return false;
            }

            if (this.queueName == null) {
                if (other.queueName != null) {
                    return false;
                }
            } else if (!this.queueName.equals(other.queueName)) {
                return false;
            }

            return true;
        }
    }
}
