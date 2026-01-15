package com.healthreminder.service;

import com.healthreminder.model.ReminderConfig;
import com.healthreminder.settings.ReminderSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;

import java.util.List;

/**
 * 提醒服务 - 主服务,协调所有提醒功能
 */
@Service
public final class ReminderService {
    
    private static final Logger LOG = Logger.getInstance(ReminderService.class);
    private boolean isRunning = false;

    public static ReminderService getInstance() {
        return ApplicationManager.getApplication().getService(ReminderService.class);
    }

    /**
     * 启动所有提醒
     */
    public void startAllReminders() {
        if (isRunning) {
            LOG.info("Reminders are already running");
            return;
        }

        ReminderSettings settings = ReminderSettings.getInstance();
        
        // 初始化默认配置(如果是第一次运行)
        settings.initializeDefaults();
        
        // 如果全局暂停,则不启动
        if (settings.globalPaused) {
            LOG.info("Reminders are globally paused");
            return;
        }

        List<ReminderConfig> configs = settings.getAllReminders();
        SchedulerService scheduler = SchedulerService.getInstance();
        NotificationService notificationService = NotificationService.getInstance();

        for (ReminderConfig config : configs) {
            if (config.isEnabled()) {
                scheduleReminder(config);
            }
        }

        isRunning = true;
        LOG.info("All reminders started");
    }

    /**
     * 停止所有提醒
     */
    public void stopAllReminders() {
        SchedulerService.getInstance().cancelAllReminders();
        isRunning = false;
        LOG.info("All reminders stopped");
    }

    /**
     * 暂停所有提醒
     */
    public void pauseAllReminders() {
        ReminderSettings settings = ReminderSettings.getInstance();
        settings.globalPaused = true;
        stopAllReminders();
        LOG.info("All reminders paused");
    }

    /**
     * 恢复所有提醒
     */
    public void resumeAllReminders() {
        ReminderSettings settings = ReminderSettings.getInstance();
        settings.globalPaused = false;
        startAllReminders();
        LOG.info("All reminders resumed");
    }

    /**
     * 重新加载配置并重启提醒
     */
    public void reloadReminders() {
        stopAllReminders();
        startAllReminders();
        LOG.info("Reminders reloaded");
    }

    /**
     * 调度单个提醒
     */
    private void scheduleReminder(ReminderConfig config) {
        SchedulerService scheduler = SchedulerService.getInstance();
        NotificationService notificationService = NotificationService.getInstance();

        Runnable reminderTask = () -> {
            LOG.info("Triggering reminder: " + config.getType().getDisplayName());
            
            notificationService.showReminder(
                config,
                () -> {
                    // 确认回调 - 什么都不做,继续下次提醒
                    LOG.info("Reminder confirmed: " + config.getType().getDisplayName());
                },
                () -> {
                    // 稍后提醒回调 - 5分钟后再次提醒
                    LOG.info("Reminder snoozed: " + config.getType().getDisplayName());
                    scheduler.scheduleOnce(5, () -> {
                        notificationService.showReminder(config, null, null);
                    });
                }
            );
        };

        scheduler.scheduleReminder(config, reminderTask);
    }

    /**
     * 检查是否正在运行
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 检查是否暂停
     */
    public boolean isPaused() {
        return ReminderSettings.getInstance().globalPaused;
    }

    /**
     * 获取下次提醒的剩余秒数
     */
    public long getNextReminderDelay() {
        return SchedulerService.getInstance().getNextReminderDelayAny();
    }
}
