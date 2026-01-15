package com.healthreminder.ui.statusbar;

import com.healthreminder.service.ReminderService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * 状态栏组件 - 显示下次提醒倒计时
 */
public class ReminderStatusBarWidget implements StatusBarWidget, StatusBarWidget.TextPresentation {
    
    private static final String ID = "com.healthreminder.statusbar";
    private final Project project;
    private Timer updateTimer;

    public ReminderStatusBarWidget(Project project) {
        this.project = project;
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        // 每秒更新一次显示
        updateTimer = new Timer(1000, e -> {
            // 触发状态栏更新
            if (project != null && !project.isDisposed()) {
                com.intellij.openapi.wm.StatusBar statusBar = 
                    com.intellij.openapi.wm.WindowManager.getInstance().getStatusBar(project);
                if (statusBar != null) {
                    statusBar.updateWidget(ID);
                }
            }
        });
        updateTimer.start();
    }

    @Override
    public @NotNull String ID() {
        return ID;
    }

    @Override
    public @NotNull WidgetPresentation getPresentation() {
        return this;
    }

    @Override
    public @NotNull String getText() {
        ReminderService service = ReminderService.getInstance();
        
        if (service.isPaused()) {
            return "⏸ 提醒已暂停";
        }
        
        if (!service.isRunning()) {
            return "⏹ 提醒未启动";
        }
        
        long delaySeconds = service.getNextReminderDelay();
        if (delaySeconds < 0) {
            return "⏱ 无提醒";
        }
        
        long minutes = delaySeconds / 60;
        long seconds = delaySeconds % 60;
        
        return String.format("⏱ 下次提醒: %02d:%02d", minutes, seconds);
    }

    @Override
    public float getAlignment() {
        return 0;
    }

    @Override
    public @Nullable String getTooltipText() {
        ReminderService service = ReminderService.getInstance();
        if (service.isPaused()) {
            return "健康提醒已暂停,点击恢复";
        } else if (service.isRunning()) {
            return "健康提醒运行中,点击暂停";
        } else {
            return "健康提醒未启动";
        }
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return mouseEvent -> {
            ReminderService service = ReminderService.getInstance();
            if (service.isPaused()) {
                service.resumeAllReminders();
            } else if (service.isRunning()) {
                service.pauseAllReminders();
            }
        };
    }

    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
            updateTimer = null;
        }
    }
}
