package com.example.picassotest;

import android.net.NetworkInfo;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PicassoExecutorService extends ThreadPoolExecutor {
    /**
     * 默认线程数
     */
    private static final int DEFAULT_THREAD_COUNT = 3;
    PicassoExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(), new Utils.PicassoThreadFactory());
    }

    /**
     * 负责手动调整核心线程数和最大线程数
     */
    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

    @Override
    public Future<?> submit(Runnable task) {
        PicassoFutureTask futureTask = new PicassoFutureTask((BitmapHunter) task);
        execute(futureTask);
        return futureTask;
    }
    private static final class PicassoFutureTask extends FutureTask<BitmapHunter>
            implements Comparable<PicassoFutureTask> {
        private final BitmapHunter hunter;

        public PicassoFutureTask(BitmapHunter hunter) {
            super(hunter, null);
            this.hunter = hunter;
        }

        @Override
        public int compareTo(PicassoFutureTask other) {
            PicassoTest.Priority p1 = hunter.getPriority();
            PicassoTest.Priority p2 = other.hunter.getPriority();

            // High-priority requests are "lesser" so they are sorted to the front.
            // Equal priorities are sorted by sequence number to provide FIFO ordering.
            return (p1 == p2 ? hunter.sequence - other.hunter.sequence : p2.ordinal() - p1.ordinal());
        }
    }
}
