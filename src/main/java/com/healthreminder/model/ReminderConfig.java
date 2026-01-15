package com.healthreminder.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 提醒配置类
 */
public class ReminderConfig {
    private String id;
    private ReminderType type;
    private int intervalMinutes;
    private String message;
    private boolean enabled;
    private Set<NotificationMethod> notificationMethods;

    public ReminderConfig() {
        this.id = UUID.randomUUID().toString();
        this.type = ReminderType.DRINK_WATER;
        this.intervalMinutes = 30;
        this.message = "";
        this.enabled = true;
        this.notificationMethods = new HashSet<>();
        this.notificationMethods.add(NotificationMethod.DIALOG);
    }

    public ReminderConfig(ReminderType type, int intervalMinutes, String message) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.intervalMinutes = intervalMinutes;
        this.message = message;
        this.enabled = true;
        this.notificationMethods = new HashSet<>();
        this.notificationMethods.add(NotificationMethod.DIALOG);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReminderType getType() {
        return type;
    }

    public void setType(ReminderType type) {
        this.type = type;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<NotificationMethod> getNotificationMethods() {
        return notificationMethods;
    }

    public void setNotificationMethods(Set<NotificationMethod> notificationMethods) {
        this.notificationMethods = notificationMethods;
    }

    /**
     * 获取实际显示的消息(如果自定义消息为空,则使用默认消息)
     */
    public String getDisplayMessage() {
        if (message == null || message.trim().isEmpty()) {
            return type.getDefaultMessage();
        }
        return message;
    }

    @Override
    public String toString() {
        return "ReminderConfig{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", intervalMinutes=" + intervalMinutes +
                ", message='" + message + '\'' +
                ", enabled=" + enabled +
                ", notificationMethods=" + notificationMethods +
                '}';
    }
}
