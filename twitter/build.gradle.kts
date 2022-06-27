version = "0.0.7"
description = "Kotlin Script Toolbox - Twitter Setup"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api(moduleCore)
    api("io.ktor:ktor-client-okhttp:1.6.8")
    api("com.google.code.gson:gson:2.9.0")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
