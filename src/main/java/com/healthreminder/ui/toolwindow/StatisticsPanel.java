package com.healthreminder.ui.toolwindow;

import com.healthreminder.model.ReminderRecord;
import com.healthreminder.model.ReminderType;
import com.healthreminder.service.StatisticsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 统计面板
 */
public class StatisticsPanel {
    
    private JPanel mainPanel;
    private JLabel totalCountLabel;
    private JLabel drinkWaterCountLabel;
    private JLabel eyeRestCountLabel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton clearButton;

    public StatisticsPanel() {
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部统计卡片
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        totalCountLabel = new JLabel("今日总计: 0", SwingConstants.CENTER);
        totalCountLabel.setBorder(BorderFactory.createTitledBorder("总提醒次数"));
        statsPanel.add(totalCountLabel);
        
        drinkWaterCountLabel = new JLabel("喝水: 0", SwingConstants.CENTER);
        drinkWaterCountLabel.setBorder(BorderFactory.createTitledBorder("喝水提醒"));
        statsPanel.add(drinkWaterCountLabel);
        
        eyeRestCountLabel = new JLabel("休息: 0", SwingConstants.CENTER);
        eyeRestCountLabel.setBorder(BorderFactory.createTitledBorder("休息提醒"));
        statsPanel.add(eyeRestCountLabel);
        
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // 中间历史记录表格
        String[] columnNames = {"时间", "类型", "响应"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("历史记录"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadStatistics());
        buttonPanel.add(refreshButton);
        
        clearButton = new JButton("清空历史");
        clearButton.addActionListener(e -> clearHistory());
        buttonPanel.add(clearButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStatistics() {
        StatisticsService service = StatisticsService.getInstance();
        
        // 更新统计卡片
        Map<ReminderType, Integer> stats = service.getTodayStatistics();
        int total = service.getTodayTotalCount();
        int drinkWater = stats.getOrDefault(ReminderType.DRINK_WATER, 0);
        int eyeRest = stats.getOrDefault(ReminderType.EYE_REST, 0);
        
        totalCountLabel.setText("今日总计: " + total);
        drinkWaterCountLabel.setText("喝水: " + drinkWater);
        eyeRestCountLabel.setText("休息: " + eyeRest);
        
        // 更新历史记录表格
        tableModel.setRowCount(0);
        List<ReminderRecord> records = service.getTodayRecords();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (ReminderRecord record : records) {
            Object[] row = new Object[3];
            row[0] = record.getTriggerTime().format(formatter);
            row[1] = record.getType().getDisplayName();
            row[2] = record.getResponse().getDisplayName();
            tableModel.addRow(row);
        }
    }

    private void clearHistory() {
        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            "确定要清空所有历史记录吗?",
            "确认清空",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            StatisticsService.getInstance().clearAllStatistics();
            loadStatistics();
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
