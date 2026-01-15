package com.healthreminder.ui.settings;

import com.healthreminder.model.NotificationMethod;
import com.healthreminder.model.ReminderConfig;
import com.healthreminder.model.ReminderType;
import com.healthreminder.service.ReminderService;
import com.healthreminder.settings.ReminderSettings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 设置页面面板
 */
public class ReminderSettingsPanel {
    
    private JPanel mainPanel;
    private JTable reminderTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton pauseResumeButton;
    private JLabel statusLabel;
    private JList<String> activeTimeList;
    private DefaultListModel<String> activeTimeListModel;
    
    private List<ReminderConfig> currentConfigs;

    public ReminderSettingsPanel() {
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板容器
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));

        // 状态栏面板
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel();
        updateStatusLabel();
        statusPanel.add(statusLabel);
        
        pauseResumeButton = new JButton();
        updatePauseResumeButton();
        pauseResumeButton.addActionListener(e -> togglePauseResume());
        statusPanel.add(pauseResumeButton);
        northContainer.add(statusPanel);

        // 时间段配置面板
        JPanel timeRangePanel = new JPanel(new BorderLayout(5, 5));
        timeRangePanel.setBorder(BorderFactory.createTitledBorder("启用时间段"));
        
        activeTimeListModel = new DefaultListModel<>();
        activeTimeList = new JList<>(activeTimeListModel);
        activeTimeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(activeTimeList);
        listScrollPane.setPreferredSize(new Dimension(300, 80));
        timeRangePanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel timeButtonPanel = new JPanel();
        timeButtonPanel.setLayout(new BoxLayout(timeButtonPanel, BoxLayout.Y_AXIS));
        
        JButton addTimeButton = new JButton("添加");
        addTimeButton.addActionListener(e -> addTimeRange());
        timeButtonPanel.add(addTimeButton);
        
        JButton editTimeButton = new JButton("编辑");
        editTimeButton.addActionListener(e -> editTimeRange());
        timeButtonPanel.add(editTimeButton);
        
        JButton removeTimeButton = new JButton("删除");
        removeTimeButton.addActionListener(e -> removeTimeRange());
        timeButtonPanel.add(removeTimeButton);
        
        timeRangePanel.add(timeButtonPanel, BorderLayout.EAST);
        northContainer.add(timeRangePanel);
        
        mainPanel.add(northContainer, BorderLayout.NORTH);

        // 中间表格容器
        JPanel centerContainer = new JPanel(new BorderLayout(5, 5));
        
        // 按钮面板 (移动到表格上方)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("添加提醒");
        addButton.addActionListener(e -> addReminder());
        buttonPanel.add(addButton);
        
        editButton = new JButton("编辑提醒");
        editButton.addActionListener(e -> editReminder());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("删除提醒");
        deleteButton.addActionListener(e -> deleteReminder());
        buttonPanel.add(deleteButton);
        
        centerContainer.add(buttonPanel, BorderLayout.NORTH);

        // 表格
        String[] columnNames = {"类型", "间隔(分钟)", "消息", "提醒方式", "启用"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reminderTable = new JTable(tableModel);
        reminderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reminderTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(reminderTable);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);
    }

    private void loadSettings() {
        ReminderSettings settings = ReminderSettings.getInstance();
        currentConfigs = new ArrayList<>(settings.getAllReminders());
        
        // 加载启用时间段
        activeTimeListModel.clear();
        if (settings.activeTimeRanges != null) {
            for (String range : settings.activeTimeRanges) {
                activeTimeListModel.addElement(range);
            }
        }
        
        // 清空表格
        tableModel.setRowCount(0);
        
        // 填充表格
        for (ReminderConfig config : currentConfigs) {
            addRowToTable(config);
        }
    }

    private void addRowToTable(ReminderConfig config) {
        Object[] row = new Object[5];
        row[0] = config.getType().getDisplayName();
        row[1] = config.getIntervalMinutes();
        row[2] = config.getDisplayMessage();
        row[3] = formatNotificationMethods(config.getNotificationMethods());
        row[4] = config.isEnabled() ? "是" : "否";
        tableModel.addRow(row);
    }

    private String formatNotificationMethods(Set<NotificationMethod> methods) {
        if (methods.isEmpty()) {
            return "无";
        }
        StringBuilder sb = new StringBuilder();
        for (NotificationMethod method : methods) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(method.getDisplayName());
        }
        return sb.toString();
    }

    private void addReminder() {
        ReminderEditDialog dialog = new ReminderEditDialog(null);
        dialog.pack();
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setVisible(true);
        
        ReminderConfig newConfig = dialog.getResult();
        if (newConfig != null) {
            currentConfigs.add(newConfig);
            addRowToTable(newConfig);
        }
    }

    private void editReminder() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainPanel, "请先选择一个提醒", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        ReminderConfig config = currentConfigs.get(selectedRow);
        ReminderEditDialog dialog = new ReminderEditDialog(config);
        dialog.pack();
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setVisible(true);
        
        ReminderConfig editedConfig = dialog.getResult();
        if (editedConfig != null) {
            currentConfigs.set(selectedRow, editedConfig);
            updateRowInTable(selectedRow, editedConfig);
        }
    }

    private void updateRowInTable(int row, ReminderConfig config) {
        tableModel.setValueAt(config.getType().getDisplayName(), row, 0);
        tableModel.setValueAt(config.getIntervalMinutes(), row, 1);
        tableModel.setValueAt(config.getDisplayMessage(), row, 2);
        tableModel.setValueAt(formatNotificationMethods(config.getNotificationMethods()), row, 3);
        tableModel.setValueAt(config.isEnabled() ? "是" : "否", row, 4);
    }

    private void deleteReminder() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainPanel, "请先选择一个提醒", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            "确定要删除这个提醒吗?",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            currentConfigs.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        }
    }

    private void togglePauseResume() {
        ReminderService service = ReminderService.getInstance();
        if (service.isPaused()) {
            service.resumeAllReminders();
        } else {
            service.pauseAllReminders();
        }
        updateStatusLabel();
        updatePauseResumeButton();
    }

    private void updateStatusLabel() {
        ReminderService service = ReminderService.getInstance();
        if (service.isPaused()) {
            statusLabel.setText("状态: 已暂停");
        } else if (service.isRunning()) {
            statusLabel.setText("状态: 运行中");
        } else {
            statusLabel.setText("状态: 未启动");
        }
    }

    private void updatePauseResumeButton() {
        ReminderService service = ReminderService.getInstance();
        if (service.isPaused()) {
            pauseResumeButton.setText("恢复提醒");
        } else {
            pauseResumeButton.setText("暂停提醒");
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void addTimeRange() {
        TimeRangeEditDialog dialog = new TimeRangeEditDialog(null);
        dialog.pack();
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setVisible(true);
        
        String result = dialog.getResult();
        if (result != null) {
            activeTimeListModel.addElement(result);
        }
    }

    private void editTimeRange() {
        int selectedIndex = activeTimeList.getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }
        
        String currentRange = activeTimeListModel.get(selectedIndex);
        TimeRangeEditDialog dialog = new TimeRangeEditDialog(currentRange);
        dialog.pack();
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setVisible(true);
        
        String result = dialog.getResult();
        if (result != null) {
            activeTimeListModel.set(selectedIndex, result);
        }
    }

    private void removeTimeRange() {
        int selectedIndex = activeTimeList.getSelectedIndex();
        if (selectedIndex != -1) {
            activeTimeListModel.remove(selectedIndex);
        }
    }

    public boolean isModified() {
        ReminderSettings settings = ReminderSettings.getInstance();
        List<ReminderConfig> savedConfigs = settings.getAllReminders();
        
        // 检查时间段是否变化
        List<String> currentRanges = new ArrayList<>();
        for (int i = 0; i < activeTimeListModel.getSize(); i++) {
            currentRanges.add(activeTimeListModel.get(i));
        }
        
        List<String> savedRanges = settings.activeTimeRanges != null ? settings.activeTimeRanges : new ArrayList<>();
        if (!currentRanges.equals(savedRanges)) {
            return true;
        }
        
        if (currentConfigs.size() != savedConfigs.size()) {
            return true;
        }
        
        // 简单比较 - 实际应该更详细
        return !currentConfigs.equals(savedConfigs);
    }

    public void apply() {
        ReminderSettings settings = ReminderSettings.getInstance();
        settings.saveReminders(currentConfigs);
        
        // 保存启用时间段
        List<String> rangeList = new ArrayList<>();
        for (int i = 0; i < activeTimeListModel.getSize(); i++) {
            rangeList.add(activeTimeListModel.get(i));
        }
        settings.activeTimeRanges = rangeList;
        
        // 重新加载提醒
        ReminderService.getInstance().reloadReminders();
        
        updateStatusLabel();
    }

    public void reset() {
        loadSettings();
        updateStatusLabel();
        updatePauseResumeButton();
    }
}
