package com.healthreminder.service;

import com.healthreminder.model.ReminderRecord;
import com.healthreminder.model.ReminderType;
import com.healthreminder.settings.StatisticsState;
import com.intellij.openapi.application.ApplicationManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务 - 提供统计数据查询功能
 */
public class StatisticsService {

    public static StatisticsService getInstance() {
        return ApplicationManager.getApplication().getService(StatisticsService.class);
    }

    /**
     * 获取今日统计数据
     * @return Map<ReminderType, Integer> 每种类型的提醒次数
     */
    public Map<ReminderType, Integer> getTodayStatistics() {
        Map<ReminderType, Integer> stats = new HashMap<>();
        
        // 初始化所有类型为0
        for (ReminderType type : ReminderType.values()) {
            stats.put(type, 0);
        }
        
        // 统计今日记录
        List<ReminderRecord> todayRecords = StatisticsState.getInstance().getTodayRecords();
        for (ReminderRecord record : todayRecords) {
            ReminderType type = record.getType();
            stats.put(type, stats.get(type) + 1);
        }
        
        return stats;
    }

    /**
     * 获取今日总提醒次数
     */
    public int getTodayTotalCount() {
        return StatisticsState.getInstance().getTodayRecords().size();
    }

    /**
     * 获取今日指定类型的提醒次数
     */
    public int getTodayCountByType(ReminderType type) {
        List<ReminderRecord> todayRecords = StatisticsState.getInstance().getTodayRecords();
        return (int) todayRecords.stream()
            .filter(r -> r.getType() == type)
            .count();
    }

    /**
     * 获取所有历史记录
     */
    public List<ReminderRecord> getAllRecords() {
        return StatisticsState.getInstance().getAllRecords();
    }

    /**
     * 获取今日记录
     */
    public List<ReminderRecord> getTodayRecords() {
        return StatisticsState.getInstance().getTodayRecords();
    }

    /**
     * 清空所有统计数据
     */
    public void clearAllStatistics() {
        StatisticsState.getInstance().clearAllRecords();
    }
}
