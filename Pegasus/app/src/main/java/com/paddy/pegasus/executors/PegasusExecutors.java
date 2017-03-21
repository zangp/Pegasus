package com.paddy.pegasus.executors;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by pengpeng on 2017/3/13.
 */

public class PegasusExecutors extends ThreadPoolExecutor {
    private static final int DEFAULT_THREAD_COUNT = 4;

    public PegasusExecutors() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT,
                10, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
    }

    @Override
    public Future<?> submit(Runnable task) {
        PegasusFutureTask futureTask = new PegasusFutureTask((DownloadTaskRunnable) task);
        execute(futureTask);
        return futureTask;
    }

    private static final class PegasusFutureTask extends FutureTask<DownloadTaskRunnable>
            implements Comparable<PegasusFutureTask>{

        public PegasusFutureTask(DownloadTaskRunnable hunter) {
            super(hunter, null);
        }

        @Override
        public int compareTo(PegasusFutureTask o) {
            return 0;
        }
    }
}
