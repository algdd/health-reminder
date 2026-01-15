package com.healthreminder.ui.settings;

import com.healthreminder.model.NotificationMethod;
import com.healthreminder.model.ReminderConfig;
import com.healthreminder.model.ReminderType;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 提醒编辑对话框
 */
public class ReminderEditDialog extends JDialog {
    
    private JComboBox<ReminderType> typeComboBox;
    private JSpinner intervalSpinner;
    private JTextField messageField;
    private JCheckBox enabledCheckBox;
    private JCheckBox dialogCheckBox;
    private JCheckBox balloonCheckBox;
    private JCheckBox statusBarCheckBox;
    
    private ReminderConfig result;
    private ReminderConfig originalConfig;

    public ReminderEditDialog(ReminderConfig config) {
        this.originalConfig = config;
        
        setTitle(config == null ? "添加提醒" : "编辑提醒");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        
        if (config != null) {
            loadConfig(config);
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 类型
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("提醒类型:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        typeComboBox = new JComboBox<>(ReminderType.values());
        typeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ReminderType) {
                    setText(((ReminderType) value).getDisplayName());
                }
                return this;
            }
        });
        mainPanel.add(typeComboBox, gbc);

        // 间隔时间
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("间隔时间(分钟):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        intervalSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1440, 1));
        mainPanel.add(intervalSpinner, gbc);

        // 提醒消息
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("提醒消息:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        messageField = new JTextField(20);
        mainPanel.add(messageField, gbc);

        // 提醒方式
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("提醒方式:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dialogCheckBox = new JCheckBox("弹窗对话框", true);
        balloonCheckBox = new JCheckBox("气泡通知");
        statusBarCheckBox = new JCheckBox("状态栏提示");
        methodPanel.add(dialogCheckBox);
        methodPanel.add(balloonCheckBox);
        methodPanel.add(statusBarCheckBox);
        mainPanel.add(methodPanel, gbc);

        // 启用
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("启用:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        enabledCheckBox = new JCheckBox("", true);
        mainPanel.add(enabledCheckBox, gbc);

        // 按钮
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> onOk());
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> onCancel());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);
    }

    private void loadConfig(ReminderConfig config) {
        typeComboBox.setSelectedItem(config.getType());
        intervalSpinner.setValue(config.getIntervalMinutes());
        messageField.setText(config.getMessage());
        enabledCheckBox.setSelected(config.isEnabled());
        
        Set<NotificationMethod> methods = config.getNotificationMethods();
        dialogCheckBox.setSelected(methods.contains(NotificationMethod.DIALOG));
        balloonCheckBox.setSelected(methods.contains(NotificationMethod.BALLOON));
        statusBarCheckBox.setSelected(methods.contains(NotificationMethod.STATUS_BAR));
    }

    private void onOk() {
        // 验证至少选择一种提醒方式
        if (!dialogCheckBox.isSelected() && !balloonCheckBox.isSelected() && !statusBarCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this, "请至少选择一种提醒方式", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 创建或更新配置
        if (originalConfig == null) {
            result = new ReminderConfig();
        } else {
            result = new ReminderConfig();
            result.setId(originalConfig.getId());
        }
        
        result.setType((ReminderType) typeComboBox.getSelectedItem());
        result.setIntervalMinutes((Integer) intervalSpinner.getValue());
        result.setMessage(messageField.getText().trim());
        result.setEnabled(enabledCheckBox.isSelected());
        
        Set<NotificationMethod> methods = new HashSet<>();
        if (dialogCheckBox.isSelected()) {
            methods.add(NotificationMethod.DIALOG);
        }
        if (balloonCheckBox.isSelected()) {
            methods.add(NotificationMethod.BALLOON);
        }
        if (statusBarCheckBox.isSelected()) {
            methods.add(NotificationMethod.STATUS_BAR);
        }
        result.setNotificationMethods(methods);
        
        dispose();
    }

    private void onCancel() {
        result = null;
        dispose();
    }

    public ReminderConfig getResult() {
        return result;
    }
}
