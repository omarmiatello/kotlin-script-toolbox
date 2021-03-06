version = "0.1.5"
description = "Kotlin Script Toolbox - Telegram Setup"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api(moduleCore)
    api("com.github.omarmiatello.telegram:client-jvm:6.0.1")
    api("io.ktor:ktor-client-okhttp:1.6.8")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
