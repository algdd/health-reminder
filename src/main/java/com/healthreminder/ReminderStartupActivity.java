package com.healthreminder;

import com.healthreminder.service.ReminderService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * 插件启动监听器 - 在 IDE 启动时自动启动提醒服务
 */
public class ReminderStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        // 启动提醒服务
        ReminderService.getInstance().startAllReminders();
    }
}
