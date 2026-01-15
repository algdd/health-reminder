package com.healthreminder.service;

import com.healthreminder.model.NotificationMethod;
import com.healthreminder.model.ReminderConfig;
import com.healthreminder.model.ReminderRecord;
import com.healthreminder.settings.StatisticsState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.wm.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * 通知服务 - 负责发送各种类型的提醒
 */
public class NotificationService {
    
    private static final String NOTIFICATION_GROUP_ID = "Health Reminder Notifications";

    public static NotificationService getInstance() {
        return ApplicationManager.getApplication().getService(NotificationService.class);
    }

    /**
     * 显示提醒
     * @param config 提醒配置
     * @param onConfirm 确认回调
     * @param onSnooze 稍后提醒回调
     */
    public void showReminder(ReminderConfig config, Runnable onConfirm, Runnable onSnooze) {
        for (NotificationMethod method : config.getNotificationMethods()) {
            switch (method) {
                case DIALOG:
                    showDialog(config, onConfirm, onSnooze);
                    break;
                case BALLOON:
                    showBalloon(config, onConfirm);
                    break;
                case STATUS_BAR:
                    showStatusBarFlash(config);
                    break;
            }
        }
    }

    /**
     * 显示弹窗对话框
     */
    private void showDialog(ReminderConfig config, Runnable onConfirm, Runnable onSnooze) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String title = config.getType().getDisplayName();
            String message = config.getDisplayMessage();
            
            int result = Messages.showDialog(
                message,
                title,
                new String[]{"确定", "稍后提醒(5分钟)"},
                0,
                getIconForType(config)
            );

            if (result == 0) {
                // 用户点击"确定"
                recordResponse(config, ReminderRecord.UserResponse.CONFIRMED);
                if (onConfirm != null) {
                    onConfirm.run();
                }
            } else if (result == 1) {
                // 用户点击"稍后提醒"
                recordResponse(config, ReminderRecord.UserResponse.SNOOZED);
                if (onSnooze != null) {
                    onSnooze.run();
                }
            } else {
                // 用户关闭对话框
                recordResponse(config, ReminderRecord.UserResponse.IGNORED);
            }
        });
    }

    /**
     * 显示气泡通知
     */
    private void showBalloon(ReminderConfig config, Runnable onConfirm) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String title = config.getType().getDisplayName();
            String content = config.getDisplayMessage();
            
            Notification notification = new Notification(
                NOTIFICATION_GROUP_ID,
                title,
                content,
                NotificationType.INFORMATION
            );
            
            Notifications.Bus.notify(notification);
            
            // 记录响应
            recordResponse(config, ReminderRecord.UserResponse.CONFIRMED);
            if (onConfirm != null) {
                onConfirm.run();
            }
        });
    }

    /**
     * 显示状态栏闪烁提示
     */
    private void showStatusBarFlash(ReminderConfig config) {
        ApplicationManager.getApplication().invokeLater(() -> {
            // 获取所有打开的项目
            com.intellij.openapi.project.Project[] projects = com.intellij.openapi.project.ProjectManager.getInstance().getOpenProjects();
            
            for (com.intellij.openapi.project.Project project : projects) {
                com.intellij.openapi.wm.StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                if (statusBar != null) {
                    // 在状态栏左下方显示信息
                    statusBar.setInfo("Health Reminder: " + config.getDisplayMessage());
                }
            }
            
            // 尝试闪烁主窗口
            Frame frame = WindowManager.getInstance().findVisibleFrame();
            if (frame != null) {
                frame.toFront();
            }

            recordResponse(config, ReminderRecord.UserResponse.CONFIRMED);
        });
    }

    /**
     * 获取提醒类型对应的图标
     */
    private Icon getIconForType(ReminderConfig config) {
        switch (config.getType()) {
            case DRINK_WATER:
            case EYE_REST:
                return Messages.getInformationIcon();
            default:
                return Messages.getQuestionIcon();
        }
    }

    /**
     * 记录用户响应
     */
    private void recordResponse(ReminderConfig config, ReminderRecord.UserResponse response) {
        ReminderRecord record = new ReminderRecord(
            config.getType(),
            LocalDateTime.now(),
            response
        );
        
        StatisticsState.getInstance().addRecord(record);
    }
}
