plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hero.base"
    compileSdk = Version.compileSdk

    defaultConfig {
        minSdk = Version.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    // Foundation
    implementation("androidx.appcompat:appcompat:${Version.appcompat}")

    // Architecture:

    // UI
    implementation("com.google.android.material:material:${Version.material}")

    // Behavior

    // KTX
    implementation("androidx.core:core-ktx:${Version.core_ktx}")

    // ThirdPart
    implementation("com.orhanobut:logger:${Version.logger}")
    implementation("com.google.code.gson:gson:${Version.gson}")

    // Test
    testImplementation("junit:junit:${Version.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Version.ext_junit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Version.espresso_core}")
}