// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Version.AGP apply false
    id("com.android.library") version Version.AGP apply false
    id("org.jetbrains.kotlin.android") version Version.kotlin apply false
}
buildscript {
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}