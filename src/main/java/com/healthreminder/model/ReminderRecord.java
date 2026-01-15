package com.healthreminder.model;

import java.time.LocalDateTime;

/**
 * 提醒历史记录
 */
public class ReminderRecord {
    private String id;
    private ReminderType type;
    private LocalDateTime triggerTime;
    private UserResponse response;

    public enum UserResponse {
        CONFIRMED("已确认"),
        SNOOZED("稍后提醒"),
        IGNORED("已忽略");

        private final String displayName;

        UserResponse(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public ReminderRecord() {
    }

    public ReminderRecord(ReminderType type, LocalDateTime triggerTime, UserResponse response) {
        this.type = type;
        this.triggerTime = triggerTime;
        this.response = response;
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

    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }

    public UserResponse getResponse() {
        return response;
    }

    public void setResponse(UserResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ReminderRecord{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", triggerTime=" + triggerTime +
                ", response=" + response +
                '}';
    }
}
