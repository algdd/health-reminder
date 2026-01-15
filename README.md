# Health Reminder - IntelliJ IDEA 健康提醒插件

一个帮助开发者在长时间编码时保持健康习惯的 IntelliJ IDEA 插件。

## 功能特性

### 核心功能
- ✅ **定时提醒喝水** - 默认每30分钟提醒一次
- ✅ **定时提醒休息眼睛** - 默认每20分钟提醒一次
- ✅ **自定义提醒** - 创建您自己的提醒类型
- ✅ **多种提醒方式** - 弹窗对话框、气泡通知、状态栏提示
- ✅ **暂停/恢复功能** - 随时暂停或恢复所有提醒
- ✅ **统计功能** - 查看今日提醒次数和历史记录
- ✅ **稍后提醒** - 点击"稍后提醒"按钮,5分钟后重新提醒
- ✅ **状态栏倒计时** - 实时显示下次提醒的剩余时间

### 界面组件
- **设置页面** - `File > Settings > Tools > Health Reminder`
- **统计工具窗口** - `View > Tool Windows > Health Statistics`
- **状态栏组件** - 显示倒计时,点击可快速暂停/恢复

## 安装方法

### 从源码构建

1. 克隆项目:
```bash
git clone <repository-url>
cd "Notification Plugin"
```

2. 编译并运行插件:
```bash
# Windows
gradlew.bat runIde

# Linux/Mac
./gradlew runIde
```

### 手动安装

1. 构建插件:
```bash
# Windows
gradlew.bat buildPlugin

# Linux/Mac
./gradlew buildPlugin
```

2. 在 IntelliJ IDEA 中:
   - 打开 `File > Settings > Plugins`
   - 点击齿轮图标 ⚙️ > `Install Plugin from Disk...`
   - 选择 `build/distributions/health-reminder-plugin-1.0.0.zip`
   - 重启 IDE

## 使用说明

### 首次使用

插件安装后会自动启动,默认配置:
- 喝水提醒: 每30分钟
- 休息眼睛提醒: 每20分钟
- 提醒方式: 弹窗对话框

### 配置提醒

1. 打开设置: `File > Settings > Tools > Health Reminder`
2. 在提醒列表中可以:
   - **添加** - 创建新的提醒
   - **编辑** - 修改现有提醒的间隔时间、消息和提醒方式
   - **删除** - 删除不需要的提醒
   - **暂停/恢复** - 全局暂停或恢复所有提醒

### 查看统计

1. 打开统计窗口: `View > Tool Windows > Health Statistics`
2. 查看:
   - 今日总提醒次数
   - 各类型提醒次数
   - 历史记录详情

### 状态栏功能

- 状态栏右下角显示下次提醒倒计时
- 点击状态栏图标可快速暂停/恢复提醒
- 图标状态:
  - ⏱ - 运行中
  - ⏸ - 已暂停
  - ⏹ - 未启动

## 开发指南

### 技术栈
- Java 17
- Gradle 8.5
- IntelliJ Platform Gradle Plugin 1.17.2
- IntelliJ Platform SDK 2025.3.1

### 项目结构
```
src/main/java/com/healthreminder/
├── model/              # 数据模型
├── service/            # 核心服务
├── settings/           # 持久化组件
├── ui/                 # 用户界面
│   ├── settings/       # 设置页面
│   ├── toolwindow/     # 统计工具窗口
│   └── statusbar/      # 状态栏组件
└── ReminderStartupActivity.java  # 启动监听器
```

### 构建命令
```bash
# 编译
gradlew.bat build

# 运行插件(启动沙箱 IDE)
gradlew.bat runIde

# 构建插件 ZIP
gradlew.bat buildPlugin

# 验证插件
gradlew.bat verifyPlugin
```

## 常见问题

**Q: 如何临时关闭提醒?**  
A: 在设置页面点击"暂停提醒"按钮,或点击状态栏图标。

**Q: 提醒间隔可以设置多短?**  
A: 最短1分钟,最长1440分钟(24小时)。

**Q: 统计数据保存多久?**  
A: 自动保留最近30天的记录。

**Q: 可以同时使用多种提醒方式吗?**  
A: 可以,在编辑提醒时可以同时勾选多种提醒方式。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request!

## 更新日志

### v1.0.0 (2026-01-08)
- 🎉 初始版本发布
- ✅ 支持喝水和休息眼睛提醒
- ✅ 支持自定义提醒
- ✅ 提供统计功能
- ✅ 状态栏倒计时显示
