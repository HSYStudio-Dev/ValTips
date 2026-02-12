// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // 코드 스타일 체크 (Ktlint)
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
    // 정적 분석 도구 (Detekt)
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    // hilt 플러그인
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    // KSP 플러그인
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    // serialization 플러그인
    kotlin("plugin.serialization") version "2.0.21"
}
