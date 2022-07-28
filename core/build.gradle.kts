version = "0.1.5"
description = "Kotlin Script Toolbox - Core"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    testImplementation("junit:junit:4.13")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}