import io.izzel.taboolib.gradle.RelocateJar
import java.util.jar.JarFile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.55"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}


repositories {
    mavenCentral()
    maven {
        url = uri("https://raw.github.com/objcoding/maven/master/")
    }
}

taboolib {
    install("common")
    install("common-5")
    install("module-kether")
    install("module-nms-util")
    install("module-nms")
    install("platform-bukkit")
    install("expansion-command-helper")
    classifier = null
    version = "6.0.10-91"
    //relocate("kotlin","org.fastmcmirror.ultrapay.kotlin")
    description {
        desc("Advanced AliPay/WechatPay System for Minecraft")
        name("UltraPay")
        contributors {
            name("FlyProject")
        }
    }
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.MF")
}

dependencies {
    taboo("com.egzosn:pay-java-common:2.14.5")
    taboo("com.egzosn:pay-java-wx:2.14.5")
    //taboo("com.egzosn:pay-java-ali:2.14.4-fix")
    //taboo("org.bouncycastle:bcprov-jdk15on:+")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    //taboo("com.objcoding:WXPay-SDK-Java:0.0.5")
    taboo("com.alipay.sdk:alipay-sdk-java:4.35.45.ALL")
    taboo("io.nayuki:qrcodegen:1.8.0")
    implementation(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.SF")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    exclude("META-INF/*.DSA", "META-INF/*.SF")
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.tabooproject.org/repository/releases")
            credentials {
                username = project.findProperty("taboolibUsername").toString()
                password = project.findProperty("taboolibPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}