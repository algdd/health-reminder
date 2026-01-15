plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.healthreminder"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    // 使用本地安装的 IDEA 2025.3.1
    localPath.set("D:\\IntelliJ IDEA 2025.3.1")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
        options.encoding = "UTF-8"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "21"
        }
    }

    // 禁用插件配置验证,因为我们使用 Java 21
    named("verifyPluginConfiguration") {
        enabled = false
    }

    // 禁用 buildSearchableOptions 以避免某些版本的 Gradle/IntelliJ 插件冲突导致的 IndexOutOfBoundsException
    buildSearchableOptions {
        enabled = false
    }

    runIde {
        // 允许在开发时热加载某些资源
        autoReloadPlugins.set(true)
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("253.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
