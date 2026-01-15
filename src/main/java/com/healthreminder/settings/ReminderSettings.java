package com.healthreminder.settings;

import com.healthreminder.model.NotificationMethod;
import com.healthreminder.model.ReminderConfig;
import com.healthreminder.model.ReminderType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 提醒设置持久化组件
 */
@State(
    name = "ReminderSettings",
    storages = @Storage("healthReminderSettings.xml")
)
public class ReminderSettings implements PersistentStateComponent<ReminderSettings> {
    
    public List<ReminderConfigState> reminders = new ArrayList<>();
    public boolean globalPaused = false;

    public static ReminderSettings getInstance() {
        return ApplicationManager.getApplication().getService(ReminderSettings.class);
    }

    @Nullable
    @Override
    public ReminderSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ReminderSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 初始化默认配置
     */
    public void initializeDefaults() {
        if (reminders.isEmpty()) {
            // 添加默认的喝水提醒
            ReminderConfig drinkWater = new ReminderConfig(
                ReminderType.DRINK_WATER,
                30,
                ReminderType.DRINK_WATER.getDefaultMessage()
            );
            reminders.add(ReminderConfigState.fromConfig(drinkWater));

            // 添加默认的休息眼睛提醒
            ReminderConfig eyeRest = new ReminderConfig(
                ReminderType.EYE_REST,
                20,
                ReminderType.EYE_REST.getDefaultMessage()
            );
            reminders.add(ReminderConfigState.fromConfig(eyeRest));
        }
    }

    /**
     * 获取所有提醒配置
     */
    public List<ReminderConfig> getAllReminders() {
        List<ReminderConfig> configs = new ArrayList<>();
        for (ReminderConfigState state : reminders) {
            configs.add(state.toConfig());
        }
        return configs;
    }

    /**
     * 保存提醒配置列表
     */
    public void saveReminders(List<ReminderConfig> configs) {
        reminders.clear();
        for (ReminderConfig config : configs) {
            reminders.add(ReminderConfigState.fromConfig(config));
        }
    }

    /**
     * 内部状态类,用于 XML 序列化
     */
    public static class ReminderConfigState {
        public String id;
        public String type;
        public int intervalMinutes;
        public String message;
        public boolean enabled;
        public List<String> notificationMethods = new ArrayList<>();

        public static ReminderConfigState fromConfig(ReminderConfig config) {
            ReminderConfigState state = new ReminderConfigState();
            state.id = config.getId();
            state.type = config.getType().name();
            state.intervalMinutes = config.getIntervalMinutes();
            state.message = config.getMessage();
            state.enabled = config.isEnabled();
            for (NotificationMethod method : config.getNotificationMethods()) {
                state.notificationMethods.add(method.name());
            }
            return state;
        }

        public ReminderConfig toConfig() {
            ReminderConfig config = new ReminderConfig();
            config.setId(id);
            config.setType(ReminderType.valueOf(type));
            config.setIntervalMinutes(intervalMinutes);
            config.setMessage(message);
            config.setEnabled(enabled);
            
            Set<NotificationMethod> methods = new HashSet<>();
            for (String methodName : notificationMethods) {
                methods.add(NotificationMethod.valueOf(methodName));
            }
            config.setNotificationMethods(methods);
            
            return config;
        }
    }
}
