package com.healthreminder.model;

/**
 * 提醒方式枚举
 */
public enum NotificationMethod {
    /**
     * 弹窗对话框
     */
    DIALOG("弹窗对话框"),
    
    /**
     * 气泡通知
     */
    BALLOON("气泡通知"),
    
    /**
     * 状态栏提示
     */
    STATUS_BAR("状态栏提示");

    private final String displayName;

    NotificationMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
