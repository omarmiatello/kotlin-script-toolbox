version = "0.1.1"
description = "Kotlin Script Toolbox - Twitter Setup"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api(moduleGson)
    api("io.ktor:ktor-client-okhttp:1.6.8")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
