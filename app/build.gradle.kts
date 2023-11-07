plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hero.ambition"
    compileSdk = Version.compileSdk

    defaultConfig {
        applicationId = "com.hero.ambition"
        minSdk = Version.minSdk
        targetSdk = Version.targetSdk
        versionCode = Version.versionCode
        versionName = Version.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Foundation
    implementation("androidx.appcompat:appcompat:${Version.appcompat}")
    // Architecture
    // UI
    implementation("com.google.android.material:material:${Version.material}")
    implementation("androidx.constraintlayout:constraintlayout:${Version.constraintlayout}")
    // Behavior
    // KTX
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Version.livedata_ktx}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.viewmodel_ktx}")
    implementation("androidx.core:core-ktx:${Version.core_ktx}")
    implementation("androidx.navigation:navigation-ui-ktx:${Version.navigation_ui_ktx}")
    implementation("androidx.navigation:navigation-fragment-ktx:${Version.navigation_fragment_ktx}")
    // Test
    testImplementation("junit:junit:${Version.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Version.ext_junit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Version.espresso_core}")
}