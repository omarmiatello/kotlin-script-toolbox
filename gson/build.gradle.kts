version = "0.1.3"
description = "Kotlin Script Toolbox - Gson Setup"

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
