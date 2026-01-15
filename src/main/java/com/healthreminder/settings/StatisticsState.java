package com.healthreminder.settings;

import com.healthreminder.model.ReminderRecord;
import com.healthreminder.model.ReminderType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计数据持久化组件
 */
@State(
    name = "StatisticsState",
    storages = @Storage("healthReminderStatistics.xml")
)
public class StatisticsState implements PersistentStateComponent<StatisticsState> {
    
    public List<ReminderRecordState> records = new ArrayList<>();

    public static StatisticsState getInstance() {
        return ApplicationManager.getApplication().getService(StatisticsState.class);
    }

    @Nullable
    @Override
    public StatisticsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StatisticsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 添加提醒记录
     */
    public void addRecord(ReminderRecord record) {
        records.add(ReminderRecordState.fromRecord(record));
        
        // 只保留最近30天的记录
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        records.removeIf(r -> {
            LocalDateTime time = LocalDateTime.parse(r.triggerTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return time.isBefore(thirtyDaysAgo);
        });
    }

    /**
     * 获取所有记录
     */
    public List<ReminderRecord> getAllRecords() {
        List<ReminderRecord> result = new ArrayList<>();
        for (ReminderRecordState state : records) {
            result.add(state.toRecord());
        }
        return result;
    }

    /**
     * 获取今日记录
     */
    public List<ReminderRecord> getTodayRecords() {
        LocalDate today = LocalDate.now();
        List<ReminderRecord> result = new ArrayList<>();
        
        for (ReminderRecordState state : records) {
            LocalDateTime time = LocalDateTime.parse(state.triggerTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (time.toLocalDate().equals(today)) {
                result.add(state.toRecord());
            }
        }
        
        return result;
    }

    /**
     * 清空所有记录
     */
    public void clearAllRecords() {
        records.clear();
    }

    /**
     * 内部状态类,用于 XML 序列化
     */
    public static class ReminderRecordState {
        public String id;
        public String type;
        public String triggerTime;
        public String response;

        public static ReminderRecordState fromRecord(ReminderRecord record) {
            ReminderRecordState state = new ReminderRecordState();
            state.id = record.getId();
            state.type = record.getType().name();
            state.triggerTime = record.getTriggerTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            state.response = record.getResponse().name();
            return state;
        }

        public ReminderRecord toRecord() {
            ReminderRecord record = new ReminderRecord();
            record.setId(id);
            record.setType(ReminderType.valueOf(type));
            record.setTriggerTime(LocalDateTime.parse(triggerTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            record.setResponse(ReminderRecord.UserResponse.valueOf(response));
            return record;
        }
    }
}
