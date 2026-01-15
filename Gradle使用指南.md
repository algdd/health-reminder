# Gradle 项目使用指南

## 项目已转换为 Gradle

项目现在使用 **Gradle + IntelliJ Platform Gradle Plugin** 构建系统,这是 JetBrains 官方推荐的方式。

## 快速开始

### 1. 运行插件(最简单)

```bash
# Windows
gradlew.bat runIde

# Linux/Mac
./gradlew runIde
```

这个命令会:
- 自动下载 IntelliJ IDEA SDK
- 编译所有代码
- 启动一个沙箱 IDE,插件已自动安装

### 2. 构建插件 ZIP 包

```bash
gradlew.bat buildPlugin
```

生成的文件在: `build/distributions/health-reminder-plugin-1.0.0.zip`

### 3. 验证插件

```bash
gradlew.bat verifyPlugin
```

检查插件是否符合 JetBrains 规范。

## 常用 Gradle 任务

```bash
# 查看所有任务
gradlew.bat tasks

# 编译代码
gradlew.bat build

# 清理构建
gradlew.bat clean

# 运行插件
gradlew.bat runIde

# 构建插件 ZIP
gradlew.bat buildPlugin

# 验证插件
gradlew.bat verifyPlugin

# 发布到 JetBrains Marketplace (需要配置 token)
gradlew.bat publishPlugin
```

## 在 IntelliJ IDEA 中使用

### 首次打开项目

1. **打开项目**
   - `File > Open`
   - 选择项目根目录
   - IDEA 会自动识别为 Gradle 项目

2. **等待 Gradle 同步**
   - IDEA 会自动下载依赖
   - 右下角会显示进度
   - 首次同步可能需要几分钟

3. **运行插件**
   - 打开 Gradle 工具窗口: `View > Tool Windows > Gradle`
   - 展开 `Tasks > intellij > runIde`
   - 双击运行
   - 或直接点击工具栏的运行按钮

### 自动创建的运行配置

Gradle 插件会自动创建以下运行配置:
- **runIde** - 运行插件
- **buildPlugin** - 构建插件
- **verifyPlugin** - 验证插件

在 `Run > Edit Configurations` 中可以看到这些配置。

## 项目结构

```
health-reminder-plugin/
├── build.gradle.kts          # Gradle 构建脚本
├── settings.gradle.kts       # Gradle 设置
├── gradle.properties         # Gradle 属性
├── gradlew.bat              # Gradle Wrapper (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   └── main/
│       ├── java/            # Java 源代码
│       └── resources/       # 资源文件
└── build/                   # 构建输出 (自动生成)
    ├── classes/             # 编译后的类文件
    ├── distributions/       # 插件 ZIP 包
    └── idea-sandbox/        # 沙箱 IDE
```

## 配置说明

### build.gradle.kts

主要配置项:
- `intellij.version` - IntelliJ IDEA 版本 (当前: 2025.3.1)
- `intellij.type` - IDE 类型 (IC = Community, IU = Ultimate)
- `patchPluginXml.sinceBuild` - 最低支持版本
- `patchPluginXml.untilBuild` - 最高支持版本

### gradle.properties

可以在这里配置:
- JVM 参数
- Gradle 缓存
- 插件版本号
- 平台版本

## 优势对比 Maven

✅ **自动下载 IntelliJ SDK** - 无需手动配置
✅ **官方支持** - JetBrains 官方维护
✅ **一键运行** - `gradlew runIde` 即可测试
✅ **自动打包** - 生成标准的插件 ZIP
✅ **验证工具** - 自动检查插件规范
✅ **发布集成** - 可直接发布到 Marketplace

## 常见问题

### Q: 首次运行很慢?
A: 正常现象,Gradle 需要下载:
   - Gradle 本身 (~100MB)
   - IntelliJ IDEA SDK (~500MB)
   - 其他依赖
   
   后续运行会快很多。

### Q: 如何更改 IntelliJ 版本?
A: 修改 `build.gradle.kts` 中的 `intellij.version`

### Q: 如何添加其他插件依赖?
A: 在 `build.gradle.kts` 的 `intellij.plugins` 中添加,例如:
   ```kotlin
   plugins.set(listOf("com.intellij.java"))
   ```

### Q: 构建失败怎么办?
A: 尝试:
   ```bash
   gradlew.bat clean
   gradlew.bat build --refresh-dependencies
   ```

## 下一步

1. **运行插件**: `gradlew.bat runIde`
2. **测试功能**: 在沙箱 IDE 中测试所有功能
3. **构建发布**: `gradlew.bat buildPlugin`
4. **安装使用**: 将 ZIP 包安装到真实 IDE

祝开发顺利! 🚀
