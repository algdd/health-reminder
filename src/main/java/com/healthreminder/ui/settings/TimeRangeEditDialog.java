package com.healthreminder.ui.settings;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeRangeEditDialog extends JDialog {
    private JSpinner startSpinner;
    private JSpinner endSpinner;
    private boolean confirmed = false;

    public TimeRangeEditDialog(String initialRange) {
        setTitle("编辑时间段");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JLabel("开始时间:"));
        startSpinner = createTimeSpinner();
        mainPanel.add(startSpinner);

        mainPanel.add(new JLabel("结束时间:"));
        endSpinner = createTimeSpinner();
        mainPanel.add(endSpinner);

        if (initialRange != null && !initialRange.isEmpty()) {
            parseAndSetTime(initialRange);
        }

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> {
            confirmed = true;
            setVisible(false);
        });
        
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        
        // 设置默认时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        spinner.setValue(calendar.getTime());
        
        return spinner;
    }

    private void parseAndSetTime(String range) {
        try {
            String[] parts = range.split("-");
            if (parts.length == 2) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date start = format.parse(parts[0].trim());
                Date end = format.parse(parts[1].trim());
                
                // 需要设置正确的年月日,否则Spinner可能显示怪异
                Calendar cal = Calendar.getInstance();
                Calendar timeCal = Calendar.getInstance();
                
                timeCal.setTime(start);
                cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                startSpinner.setValue(cal.getTime());
                
                timeCal.setTime(end);
                cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                endSpinner.setValue(cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        if (!confirmed) return null;
        
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String start = format.format(startSpinner.getValue());
        String end = format.format(endSpinner.getValue());
        
        return start + "-" + end;
    }
}
