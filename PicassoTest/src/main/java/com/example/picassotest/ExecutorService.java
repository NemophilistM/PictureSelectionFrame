package com.example.picassotest;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface ExecutorService extends Executor {

    /**
     * 启动有序关闭，其中执行先前提交的任务，但不会接受新任务。如果已经关闭，调用没有额外的效果。
     * 此方法不等待先前提交的任务完成执行。使用awaitTermination来做到这一点。目前感觉我大概率用不到
     */
    void shutdown();

    /**
     * 如果此执行程序已关闭，则返回true 。
     * 回报：
     * 如果此执行程序已关闭，则为true
     */
    boolean isShutdown();

    boolean isTerminated();


    boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;


    List<Runnable> shutdownNow();
    /**
     * 提交 Runnable 任务以执行并返回代表该任务的 Future。 Future 的get方法将在成功完成后返回null 。
     * 参数：
     * task – 要提交的任务
     * 回报：
     * 代表待完成任务的 Future
     * 抛出：
     * RejectedExecutionException – 如果无法安排任务执行
     * NullPointerException – 如果任务为空
     */
    Future<?> submit(Runnable task);

}
