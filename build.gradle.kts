@file:Suppress("PublicApiImplicitType")

import org.jetbrains.kotlin.cli.jvm.compiler.jvmFactories
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.incremental.withJsIC

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

val javaVersion = "11"
val flexmarkVersion = "0.64.0"
val pluginSinceBuild = "203"
val pluginUntilBuild = ""
val pluginVersion = "3.0.203.115"

//ant.importBuild("release.xml")

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.20"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.vladsch.idea.multimarkdown"
version = pluginVersion

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2020.3.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("IntelliLang", "git4idea", "java", "grazie", "platform-images"))
}

dependencies {
    annotationProcessor("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")

    compileOnly(
        files(
            "lib/jfxrt-1.8.0.jar"
        )
    )

    implementation("com.vladsch.flexmark:flexmark-all:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-util:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-tree-iteration:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-test-util:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-zzzzzz:$flexmarkVersion")
    implementation("com.vladsch.flexmark:flexmark-ext-spec-example:$flexmarkVersion")

    implementation("com.jgoodies:jgoodies-common:1.8.1")
    implementation("com.vladsch.javafx-webview-debugger:javafx-webview-debugger:0.8.6")
    implementation("com.vladsch.boxed-json:boxed-json:0.5.32")
    implementation("com.vladsch.reverse-regex:reverse-regex-util:0.3.6")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.jsoup:jsoup:1.15.4")

    implementation(
        files(
            "lib/plantuml-jar-asl-1.2020.6.jar",
            "lib/plugin-util.jar",
            "lib/plugin-test-util.jar",
        )
    )
}

sourceSets {
    main {
        java {
            setSrcDirs(mutableListOf("src/main/java"))
            resources.setSrcDirs(mutableListOf("src/main/resources", "src/main/resources-flex"))
        }
        
        kotlin {
            setSrcDirs(mutableListOf("src/main/java"))
        }
    }

    test {
        java {
            setSrcDirs(mutableListOf("src/test/java"))
        }
        
        kotlin {
            setSrcDirs(mutableListOf("src/test/java"))
        }
    }
    
}

tasks { // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }
    
    processResources {
         
    }

    patchPluginXml {
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)
        version.set(pluginVersion)
    }

    buildPlugin {

    }

    runPluginVerifier {

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
