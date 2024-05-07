import com.android.build.api.dsl.AaptOptions
import com.android.build.api.dsl.AndroidResources
import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("com.mob.sdk")
}


android {
    namespace = "com.example.smartstore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartstore"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


//    fun AndroidResources.() {
//        noCompress += listOf("tflite")
//    }
//    androidResources


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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation( "jackmego.com.jieba_android:jieba_android-release:0.1.0");
    implementation ("org.tensorflow:tensorflow-lite:+")
    implementation ("androidx.camera:camera-core:1.2.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("androidx.core:core:1.13.0")
    testImplementation("junit:junit:4.13.2")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation ("androidx.fragment:fragment:1.4.1")
//    implementation("androidx.preference:Apreference:1.2.1")
//    implementation ("org.tensorflow:tensorflow-lite-task-vision:+")
//    implementation ("org.tensorflow:tensorflow-lite-task-text:+")
//    implementation ("org.tensorflow:tensorflow-lite-task-audio:+")
//    implementation ("org.tensorflow:tensorflow-lite:+")
//    implementation("com.android.support:design:28.0.0")
//    implementation("androidx.navigation:navigation-fragment:2.6.0")
//    implementation ("androidx.camera:camera-lifecycle:1.2.2")
//    implementation ("androidx.camera:camera-view:1.2.2")
//    implementation ("androidx.camera:camera-camera2:1.2.2")
    MobSDK {
        appKey = "39792fe74ca6a"
        appSecret = "69edb1dfeda3bed071eac4649b48579c"
        SMSSDK(null)
    }
}


