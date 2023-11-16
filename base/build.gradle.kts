plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("maven-publish") // 引入 maven 插件
}

val GROUP_ID = "com.hero.ambition"
val ARTIFACT_ID = "base-utils"
val VERSION = "0.0.3"

//val VERSION = latestGitTag().ifEmpty { "0.0.2" }

fun latestGitTag(): String {
    val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
    return process.inputStream.bufferedReader().use {bufferedReader ->
        bufferedReader.readText().trim()
    }
}

afterEvaluate {
    // 官方建议使用上传方法
    publishing {
        publications {
            // Creates a Maven publication called "release".
            register<MavenPublication>("release") {
                groupId = GROUP_ID //groupId 随便取 , 这个是依赖库的组 id
                artifactId = ARTIFACT_ID  //artifactId 随便取 , 依赖库的名称（jitpack 都不会使用到）
                version = VERSION // 当前版本依赖库版本号，这个jitpack不会使用到，只是我们开发者自己查看
            }
        }
    }
}

//publishing { // 发布配置
//    publications { // 发布的内容
//        register<MavenPublication>("release") { // 注册一个名字为 release 的发布内容
//            groupId = GROUP_ID
//            artifactId = ARTIFACT_ID
//            version = VERSION
//
//            afterEvaluate { // 在所有的配置都完成之后执行
//                // 从当前 module 的 release 包中发布
//                from(components["release"])
//            }
//        }
//    }
//}

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
    implementation("com.guolindev.permissionx:permissionx:${Version.permissionx}")

    // Test
    testImplementation("junit:junit:${Version.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Version.ext_junit}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Version.espresso_core}")
}