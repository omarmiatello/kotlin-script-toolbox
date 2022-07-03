version = "0.1.3"
description = "Kotlin Script Toolbox - Zero Setup"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api(moduleGson)
    api(moduleTelegram)
    api(moduleTwitter)
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
