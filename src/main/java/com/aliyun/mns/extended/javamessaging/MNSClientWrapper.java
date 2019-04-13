//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.model.QueueMeta;

public class MNSClientWrapper {
    private MNSClient mnsClient = null;

    public MNSClientWrapper(String accessKeyId, String accessKeySecret, String accountEndpoint, ClientConfiguration config) {
        this.mnsClient = (new CloudAccount(accessKeyId, accessKeySecret, accountEndpoint, config)).getMNSClient();
    }

    public void createQueue(String queueName) {
        QueueMeta meta = new QueueMeta();
        meta.setQueueName(queueName);
        this.mnsClient.createQueue(meta);
    }

    public MNSQueueWrapper generateMNSQueueWrapper(String queue) {
        CloudQueue cloudQueue = this.mnsClient.getQueueRef(queue);
        return new MNSQueueWrapper(cloudQueue);
    }

    public void destory() {
        if (this.mnsClient != null) {
            this.mnsClient.close();
        }

    }
}
