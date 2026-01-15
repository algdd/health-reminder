# Java 17 环境配置说明

## 问题

Gradle 仍然使用 Java 8,即使您已经切换到 Java 17。这是因为:
1. Gradle Daemon 已经启动并缓存了旧的 Java 版本
2. 当前 PowerShell 会话的环境变量还没有更新

## 解决方案

### 方法一:重新打开 PowerShell (推荐)

1. **关闭当前 PowerShell 窗口**
2. **重新打开 PowerShell**
3. **进入项目目录**:
   ```powershell
   cd "d:\selfCode\Notification Plugin"
   ```
4. **验证 Java 版本**:
   ```powershell
   java -version
   ```
   应该显示 `java version "17.x.x"`

5. **运行插件**:
   ```powershell
   .\gradlew.bat runIde
   ```

### 方法二:在当前窗口刷新环境变量

```powershell
# 刷新环境变量
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# 验证 Java 版本
java -version

# 停止所有 Gradle Daemon
.\gradlew.bat --stop

# 运行插件
.\gradlew.bat runIde
```

### 方法三:在 IntelliJ IDEA 中运行 (最简单)

1. **在 IntelliJ IDEA 中打开项目**
   - `File > Open`
   - 选择 `d:\selfCode\Notification Plugin`

2. **等待 Gradle 同步完成**
   - IDEA 会自动检测并使用正确的 Java 版本

3. **运行插件**
   - 打开 Gradle 工具窗口: `View > Tool Windows > Gradle`
   - 展开 `Tasks > intellij > runIde`
   - 双击运行

## 验证步骤

### 1. 检查 Java 版本
```powershell
java -version
```
应该显示:
```
java version "17.x.x"
Java(TM) SE Runtime Environment (build 17.x.x)
Java HotSpot(TM) 64-Bit Server VM (build 17.x.x, mixed mode, sharing)
```

### 2. 检查 JAVA_HOME
```powershell
echo $env:JAVA_HOME
```
应该指向 Java 17 的安装目录

### 3. 检查 Gradle 使用的 Java
```powershell
.\gradlew.bat -version
```
应该显示使用 Java 17

## 预期结果

运行 `.\gradlew.bat runIde` 后,您应该看到:
1. Gradle 开始下载 IntelliJ IDEA SDK (~500MB,首次需要时间)
2. 编译所有 Java 代码
3. 启动一个新的 IntelliJ IDEA 窗口(沙箱环境)
4. 插件已自动安装在沙箱 IDE 中

## 常见问题

### Q: 为什么需要 Java 17?
A: IntelliJ Platform Gradle Plugin 1.17.4 需要 Java 11+,而我们的代码使用 Java 17 编译。

### Q: 下载很慢怎么办?
A: 已配置腾讯云镜像加速。首次下载 IntelliJ SDK 需要一些时间,请耐心等待。

### Q: 可以使用 Java 11 吗?
A: 可以,但需要修改 `build.gradle.kts` 中的 Java 版本配置。

## 下一步

成功运行后,您将看到沙箱 IDE 启动,然后可以:
1. 打开 `File > Settings > Tools > Health Reminder` 配置提醒
2. 打开 `View > Tool Windows > Health Statistics` 查看统计
3. 查看状态栏右下角的倒计时

祝测试顺利! 🎉
