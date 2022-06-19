import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

val DependencyHandler.moduleCore get() = project(":core")
val DependencyHandler.moduleExample get() = project(":example")
val DependencyHandler.moduleZeroSetup get() = project(":zero-setup")