plugins {
    kotlin("jvm")
}

dependencies {
    implementation(moduleZeroSetup)
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
