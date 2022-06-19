version = "0.0.3"
description = "Kotlin Script Toolbox - Zero Setup"

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    publish
}

dependencies {
    api(moduleCore)
    api("com.google.code.gson:gson:2.9.0")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
