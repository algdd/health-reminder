package com.healthreminder.model;

/**
 * 提醒类型枚举
 */
public enum ReminderType {
    /**
     * 喝水提醒
     */
    DRINK_WATER("喝水提醒", "该喝水了!💧", 30),
    
    /**
     * 休息眼睛提醒
     */
    EYE_REST("休息眼睛", "休息一下眼睛吧!👀", 20),
    
    /**
     * 自定义提醒
     */
    CUSTOM("自定义提醒", "提醒时间到!", 60);

    private final String displayName;
    private final String defaultMessage;
    private final int defaultIntervalMinutes;

    ReminderType(String displayName, String defaultMessage, int defaultIntervalMinutes) {
        this.displayName = displayName;
        this.defaultMessage = defaultMessage;
        this.defaultIntervalMinutes = defaultIntervalMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public int getDefaultIntervalMinutes() {
        return defaultIntervalMinutes;
    }
}
