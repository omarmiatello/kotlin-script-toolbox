plugins {
    kotlin("jvm")
}

dependencies {
    implementation(moduleZeroSetup)
    // implementation("com.github.omarmiatello.kotlin-script-toolbox:zero-setup:0.0.3")
}

java {
    val javaVersion = JavaVersion.toVersion("11")
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
