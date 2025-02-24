/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.12.1/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation(libs.arrow.vector)
    implementation(libs.arrow.memory.netty)

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val nativeBuildSrc = "parquet-arrow-rs"
val nativeBuildTgt = layout.buildDirectory.dir("native")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
    sourceSets {
        main {
            resources.srcDirs("resources", nativeBuildTgt)
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.named("processResources") {
    dependsOn("buildNativeLib")
}
tasks.named("compileJava") {
    dependsOn("buildNativeLib")
}
tasks.register<Copy>("buildNativeLib") {
    dependsOn("buildRust")
    from("$nativeBuildSrc/target/release") {
        include("*.so", "*.dylib", "*.dll")
    }
    into(nativeBuildTgt)
}
tasks.register<BuildRust>("buildRust")

tasks.named("clean") {
    dependsOn("cleanRustBuild")
}
tasks.register<CleanRustBuild>("cleanRustBuild")

abstract class BaseBuild : DefaultTask() {
    @Input
    val pkg = "parquet-arrow-rs"
}

abstract class BuildRust : BaseBuild() {
    @TaskAction
    fun action() {
        val buildCmd = listOf(
            "bash", "-c", "cargo build --release --manifest-path $pkg/Cargo.toml"
        )
        val process = ProcessBuilder(buildCmd).start()
        val exit = process.waitFor()
        if (exit != 0) {
            process.errorReader().use { reader ->
                reader.lines().forEach {
                    println(it)
                }
            }
        }
    }
}

abstract class CleanRustBuild : BaseBuild() {
    @TaskAction
    fun action() {
        val buildCmd = listOf("bash", "-c", "cargo clean --manifest-path $pkg/Cargo.toml")
        val process = ProcessBuilder(buildCmd).start()
        val exit = process.waitFor()
        if (exit != 0) {
            process.errorReader().use { reader ->
                reader.lines().forEach {
                    println(it)
                }
            }
        }
    }
}
