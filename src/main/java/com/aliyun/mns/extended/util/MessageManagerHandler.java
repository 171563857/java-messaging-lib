//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.util;

import com.aliyun.mns.extended.fetcher.MessageFetcher;
import com.aliyun.mns.extended.fetcher.MessageFetcher.MessageManager;
import com.aliyun.mns.extended.javamessaging.MNSQueueWrapper;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage;
import java.util.ArrayDeque;
import javax.jms.JMSException;

public class MessageManagerHandler {
    private static final int VISIBILITY_TIME_OUT = 1;

    public MessageManagerHandler() {
    }

    public void setVisiable(ArrayDeque<MessageManager> messageQueue) throws JMSException {
        while(!messageQueue.isEmpty()) {
            MessageManager manager = (MessageManager)messageQueue.pollFirst();
            this.setVisible(manager);
        }

    }

    public void setVisible(MessageManager manager) throws JMSException {
        MNSMessage message = (MNSMessage)manager.getMessage();
        String receiptHandle = message.getReceiptHandle();
        MessageFetcher prefetcher = (MessageFetcher)manager.getPrefetchManager();
        MNSQueueWrapper queue = prefetcher.getQueue();
        queue.changeMessageVisibilityTimeout(receiptHandle, 1);
    }

    public void deleteMessage(MessageManager manager) throws JMSException {
        MNSMessage message = (MNSMessage)manager.getMessage();
        String receiptHandle = message.getReceiptHandle();
        MessageFetcher prefetcher = (MessageFetcher)manager.getPrefetchManager();
        prefetcher.getQueue().deleteMessage(receiptHandle);
    }
}
