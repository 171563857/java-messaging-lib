//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.fetcher;

import com.aliyun.mns.extended.javamessaging.MNSMessageConsumer;

public interface MessageFetcherManager {
    void messageDispatched();

    MNSMessageConsumer getConsumer();
}
