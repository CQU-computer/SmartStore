// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false  //?????版本?????
}
buildscript {
    dependencies {
        // 增加MobSDK插件配置
        classpath("com.mob.sdk:MobSDK2:+")
        // 增加google services插件配置，用于集成FCM，不集成FCM可不配置
//        classpath("com.google.gms:google-services:4.3.14")
    }
}

