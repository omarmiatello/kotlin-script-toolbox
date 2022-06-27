import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

val DependencyHandler.moduleCore get() = project(":core")
val DependencyHandler.moduleExample get() = project(":example")
val DependencyHandler.moduleTelegram get() = project(":telegram")
val DependencyHandler.moduleTwitter get() = project(":twitter")
val DependencyHandler.moduleZeroSetup get() = project(":zero-setup")