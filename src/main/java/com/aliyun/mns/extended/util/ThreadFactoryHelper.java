//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactoryHelper implements ThreadFactory {
    private final String threadBaseName;
    private final AtomicInteger threadCounter;
    private final boolean isDaemon;
    private ThreadGroup threadGroup;

    public ThreadFactoryHelper(String taskName, boolean isDaemon) {
        this(taskName, isDaemon, false);
    }

    public ThreadFactoryHelper(String taskName, boolean isDaemon, boolean createWithThreadGroup) {
        this.threadBaseName = taskName + "Thread-";
        this.threadCounter = new AtomicInteger(0);
        this.isDaemon = isDaemon;
        if (createWithThreadGroup) {
            this.threadGroup = new ThreadGroup(taskName + "ThreadGroup");
            this.threadGroup.setDaemon(isDaemon);
        }

    }

    public ThreadFactoryHelper(String taskName, ThreadGroup threadGroup) {
        this.threadBaseName = taskName + "Thread-";
        this.threadCounter = new AtomicInteger(0);
        this.isDaemon = threadGroup.isDaemon();
        this.threadGroup = threadGroup;
    }

    public Thread newThread(Runnable r) {
        Thread t;
        if (this.threadGroup == null) {
            t = new Thread(r, this.threadBaseName + this.threadCounter.incrementAndGet());
            t.setDaemon(this.isDaemon);
        } else {
            t = new Thread(this.threadGroup, r, this.threadBaseName + this.threadCounter.incrementAndGet());
            t.setDaemon(this.isDaemon);
        }

        return t;
    }

    public boolean wasThreadCreatedWithThisThreadGroup(Thread thread) {
        if (this.threadGroup == null) {
            return false;
        } else {
            return thread.getThreadGroup() == this.threadGroup;
        }
    }
}
