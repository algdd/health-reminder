# 开发说明

## Maven 依赖问题

由于 Maven 中央仓库和 JetBrains 仓库都不提供 IntelliJ IDEA 的完整 JAR 包依赖,使用纯 Maven 方式无法直接编译插件。

### 解决方案

有以下几种方式来开发和运行此插件:

#### 方案一:使用 IntelliJ IDEA 的 Plugin DevKit (推荐)

1. 在 IntelliJ IDEA 中打开项目
2. 确保安装了 "Plugin DevKit" 插件
3. 配置 IntelliJ Platform SDK:
   - `File > Project Structure > SDKs`
   - 添加 "IntelliJ Platform Plugin SDK"
   - 选择您的 IntelliJ IDEA 安装目录
4. 配置项目 SDK:
   - `File > Project Structure > Project`
   - 设置 Project SDK 为刚才配置的 IntelliJ Platform Plugin SDK
5. 创建运行配置:
   - `Run > Edit Configurations`
   - 添加 "Plugin" 配置
   - 运行即可启动沙箱 IDE

#### 方案二:转换为 Gradle 项目 (官方推荐)

JetBrains 官方推荐使用 Gradle + IntelliJ Platform Gradle Plugin 来开发插件。

1. 删除 `pom.xml`
2. 创建 `build.gradle.kts`:

```kotlin
plugins {
    id("java")
    id("org.jetbrains.intellij") version "2.10.4"
}

group = "com.healthreminder"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2025.3.1")
    type.set("IC")
    plugins.set(listOf())
}

tasks {
    patchPluginXml {
        sinceBuild.set("253")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
```

3. 创建 `settings.gradle.kts`:
```kotlin
rootProject.name = "health-reminder-plugin"
```

4. 运行 `./gradlew runIde`

#### 方案三:使用本地 SDK 依赖 (当前 Maven 项目)

1. 手动下载 IntelliJ IDEA Community Edition
2. 将 IDEA 安装目录下的 JAR 包添加为本地依赖
3. 修改 `pom.xml`,使用 `system` scope 引用本地 JAR

```xml
<dependency>
    <groupId>com.jetbrains</groupId>
    <artifactId>idea</artifactId>
    <version>local</version>
    <scope>system</scope>
    <systemPath>${idea.home}/lib/idea.jar</systemPath>
</dependency>
```

## 当前项目状态

### 已完成
- ✅ 所有 Java 源代码文件
- ✅ 数据模型 (ReminderType, ReminderConfig, NotificationMethod, ReminderRecord)
- ✅ 核心服务 (ReminderService, SchedulerService, NotificationService, StatisticsService)
- ✅ 持久化组件 (ReminderSettings, StatisticsState)
- ✅ UI 组件 (设置页面、统计工具窗口、状态栏组件)
- ✅ 插件配置文件 (plugin.xml)
- ✅ 启动监听器
- ✅ 图标资源
- ✅ README 文档

### 功能特性
- 喝水提醒 (默认30分钟)
- 休息眼睛提醒 (默认20分钟)
- 自定义提醒
- 三种提醒方式 (弹窗/气泡/状态栏)
- 暂停/恢复功能
- 统计功能
- "稍后提醒" (5分钟)
- 状态栏倒计时

## 下一步

建议使用 **方案一 (Plugin DevKit)** 来运行和测试插件,这是最简单直接的方式。

或者如果您希望使用 Gradle,我可以帮您转换项目结构。
