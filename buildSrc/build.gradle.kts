plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.21")
    implementation("org.jetbrains.dokka:dokka-core:1.6.21")
}