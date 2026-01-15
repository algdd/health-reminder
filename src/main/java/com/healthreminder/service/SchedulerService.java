package com.healthreminder.service;

import com.healthreminder.model.ReminderConfig;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.Disposable;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 调度服务 - 管理定时任务
 */
public class SchedulerService implements Disposable {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public static SchedulerService getInstance() {
        return ApplicationManager.getApplication().getService(SchedulerService.class);
    }

    /**
     * 调度一个提醒任务
     * @param config 提醒配置
     * @param task 要执行的任务
     */
    public void scheduleReminder(ReminderConfig config, Runnable task) {
        // 取消已存在的任务
        cancelReminder(config.getId());
        
        // 创建新的定时任务
        long intervalMillis = config.getIntervalMinutes() * 60 * 1000L;
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            task,
            intervalMillis,  // 初始延迟
            intervalMillis,  // 间隔时间
            TimeUnit.MILLISECONDS
        );
        
        scheduledTasks.put(config.getId(), future);
    }

    /**
     * 调度一次性任务(用于"稍后提醒")
     * @param delayMinutes 延迟分钟数
     * @param task 要执行的任务
     */
    public void scheduleOnce(int delayMinutes, Runnable task) {
        long delayMillis = delayMinutes * 60 * 1000L;
        scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 取消指定的提醒任务
     */
    public void cancelReminder(String reminderId) {
        ScheduledFuture<?> future = scheduledTasks.remove(reminderId);
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }

    /**
     * 取消所有提醒任务
     */
    public void cancelAllReminders() {
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            if (!future.isCancelled()) {
                future.cancel(false);
            }
        }
        scheduledTasks.clear();
    }

    /**
     * 获取下次提醒的剩余时间(秒)
     * @param reminderId 提醒ID
     * @return 剩余秒数,如果任务不存在返回-1
     */
    public long getNextReminderDelay(String reminderId) {
        ScheduledFuture<?> future = scheduledTasks.get(reminderId);
        if (future != null && !future.isCancelled()) {
            return future.getDelay(TimeUnit.SECONDS);
        }
        return -1;
    }

    /**
     * 获取最近的下次提醒时间(秒)
     */
    public long getNextReminderDelayAny() {
        long minDelay = Long.MAX_VALUE;
        for (ScheduledFuture<?> future : scheduledTasks.values()) {
            if (!future.isCancelled()) {
                long delay = future.getDelay(TimeUnit.SECONDS);
                if (delay > 0 && delay < minDelay) {
                    minDelay = delay;
                }
            }
        }
        return minDelay == Long.MAX_VALUE ? -1 : minDelay;
    }

    @Override
    public void dispose() {
        cancelAllReminders();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
