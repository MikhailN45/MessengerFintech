plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.study.messengerfintech"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.study.messengerfintech"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.useJUnit()
        }
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }
}

dependencies {
    //by default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)

    //firebase analytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)


    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //rx
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.rxkotlin)
    implementation(libs.adapter.rxjava2)

    //network
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.mockwebserver)

    //UI
    implementation(libs.shimmer)
    implementation(libs.bumptechGlide)

    //DI
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rxjava2)
    ksp(libs.androidx.room.compiler)

    //Tests
    testImplementation(libs.junit)
    testImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.fragment.testing)
    debugImplementation(libs.androidx.fragment.testing)
    androidTestUtil(libs.androidx.orchestrator)

    //kotest
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)

    //mockk
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.mockito.core)

    //kaspresso
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.hamcrest)

    testImplementation(libs.testing.ktx)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.core.testing)
}