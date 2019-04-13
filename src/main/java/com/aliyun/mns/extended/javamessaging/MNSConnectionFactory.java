//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.common.http.ClientConfiguration;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;

public class MNSConnectionFactory implements ConnectionFactory, QueueConnectionFactory {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private ClientConfiguration config = null;

    public MNSConnectionFactory(MNSConnectionFactory.Builder builder) {
        this.accessKeyId = builder.accessKeyId;
        this.accessKeySecret = builder.accessKeySecret;
        this.endpoint = builder.endpoint;
        this.config = builder.config;
    }

    public Connection createConnection() throws JMSException {
        return this.createConnection(this.accessKeyId, this.accessKeySecret);
    }

    public Connection createConnection(String userName, String password) throws JMSException {
        MNSClientWrapper clientWrapper = new MNSClientWrapper(userName, password, this.endpoint, this.config);
        return new MNSQueueConnection(clientWrapper);
    }

    public MNSQueueConnection createQueueConnection() throws JMSException {
        return (MNSQueueConnection)this.createConnection();
    }

    public MNSQueueConnection createQueueConnection(String userName, String password) throws JMSException {
        return (MNSQueueConnection)this.createConnection(userName, password);
    }

    public static MNSConnectionFactory.Builder builder() {
        return new MNSConnectionFactory.Builder();
    }

    public static class Builder {
        public String accessKeySecret;
        public String accessKeyId;
        private String endpoint;
        private ClientConfiguration config = null;

        public Builder() {
        }

        public MNSConnectionFactory.Builder withAccessKeyId(String accessKeyId) {
            this.setAccessKeyId(accessKeyId);
            return this;
        }

        public MNSConnectionFactory.Builder withAccessKeySecret(String accessKeySecret) {
            this.setAccessKeySecret(accessKeySecret);
            return this;
        }

        public MNSConnectionFactory.Builder withEndpoint(String endpoint) {
            this.setEndpoint(endpoint);
            return this;
        }

        public String getAccessKeySecret() {
            return this.accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getAccessKeyId() {
            return this.accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getEndpoint() {
            return this.endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public ClientConfiguration getClientConfiguration() {
            return this.config;
        }

        public void setClientConfiguration(ClientConfiguration config) {
            this.config = config;
        }

        public MNSConnectionFactory build() {
            return new MNSConnectionFactory(this);
        }
    }
}
