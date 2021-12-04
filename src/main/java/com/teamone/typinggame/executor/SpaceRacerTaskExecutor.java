package com.teamone.typinggame.executor;

import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.*;

public class SpaceRacerTaskExecutor extends ThreadPoolTaskExecutor {
    @Override
    public Future<?> submit(Runnable task) {
        System.out.println("SUBMIT RUNNABLE");
        return super.submit(task);
    }

    public SpaceRacerTaskExecutor() {
        super();
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        super.setCorePoolSize(corePoolSize);
    }

    @Override
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        super.setMaxPoolSize(maxPoolSize);
    }

    @Override
    public int getMaxPoolSize() {
        return super.getMaxPoolSize();
    }

    @Override
    public void setKeepAliveSeconds(int keepAliveSeconds) {
        super.setKeepAliveSeconds(keepAliveSeconds);
    }

    @Override
    public int getKeepAliveSeconds() {
        return super.getKeepAliveSeconds();
    }

    @Override
    public void setQueueCapacity(int queueCapacity) {
        super.setQueueCapacity(queueCapacity);
    }

    @Override
    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        super.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }

    @Override
    public void setTaskDecorator(TaskDecorator taskDecorator) {
        super.setTaskDecorator(taskDecorator);
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return super.initializeExecutor(threadFactory, rejectedExecutionHandler);
    }

    @Override
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        return super.createQueue(queueCapacity);
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        return super.getThreadPoolExecutor();
    }

    @Override
    public int getPoolSize() {
        return super.getPoolSize();
    }

    @Override
    public int getActiveCount() {
        return super.getActiveCount();
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        System.out.println("TASK WITH TIMEOUT");
        super.execute(task, startTimeout);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        System.out.println("SUBMIT CALLABLE");
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        System.out.println("SUBMIT LISTENABLE RUNNABLE");
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        System.out.println("SUBMIT LISTENABLE CALLABLE");
        return super.submitListenable(task);
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        System.out.println("CANCEL REMAINING TASK");
        super.cancelRemainingTask(task);
    }

    @Override
    public boolean prefersShortLivedTasks() {
        return super.prefersShortLivedTasks();
    }

    @Override
    public void setThreadFactory(ThreadFactory threadFactory) {
        super.setThreadFactory(threadFactory);
    }

    @Override
    public void setThreadNamePrefix(String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix);
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        super.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    @Override
    public void setWaitForTasksToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
        super.setWaitForTasksToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
    }

    @Override
    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        super.setAwaitTerminationSeconds(awaitTerminationSeconds);
    }

    @Override
    public void setAwaitTerminationMillis(long awaitTerminationMillis) {
        super.setAwaitTerminationMillis(awaitTerminationMillis);
    }

    @Override
    public void setBeanName(String name) {
        super.setBeanName(name);
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return super.newThread(runnable);
    }

    @Override
    public String getThreadNamePrefix() {
        return super.getThreadNamePrefix();
    }

    @Override
    public void setThreadPriority(int threadPriority) {
        super.setThreadPriority(threadPriority);
    }

    @Override
    public int getThreadPriority() {
        return super.getThreadPriority();
    }

    @Override
    public void setDaemon(boolean daemon) {
        super.setDaemon(daemon);
    }

    @Override
    public boolean isDaemon() {
        return super.isDaemon();
    }

    @Override
    public void setThreadGroupName(String name) {
        super.setThreadGroupName(name);
    }

    @Override
    public void setThreadGroup(ThreadGroup threadGroup) {
        super.setThreadGroup(threadGroup);
    }

    @Override
    public ThreadGroup getThreadGroup() {
        return super.getThreadGroup();
    }

    @Override
    public Thread createThread(Runnable runnable) {
        System.out.println("CREATE THREAD");
        return super.createThread(runnable);
    }

    @Override
    protected String nextThreadName() {
        return super.nextThreadName();
    }

    @Override
    protected String getDefaultThreadNamePrefix() {
        return super.getDefaultThreadNamePrefix();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void execute(Runnable task) {
        System.out.println("EXECUTE TASK " + task);
        super.execute(task);
    }
}
