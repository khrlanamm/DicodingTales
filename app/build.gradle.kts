plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("kotlin-parcelize")
}

android {
    namespace = "com.khrlanamm.dicodingtales"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.khrlanamm.dicodingtales"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //data from API
    implementation("com.google.code.gson:gson:2.11.0") // Library Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // Library Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // Converter Gson untuk Retrofit
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Library OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Logging Interceptor untuk OkHttp
    implementation("com.github.bumptech.glide:glide:4.16.0") // Library Glide
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Compiler untuk Glide

    //livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7") // LiveData KTX dari Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7") // ViewModel KTX dari Lifecycle
    implementation("androidx.activity:activity-ktx:1.9.3") // Activity KTX
    implementation("androidx.recyclerview:recyclerview:1.3.2") // RecyclerView
    implementation("androidx.legacy:legacy-support-v4:1.0.0") // Legacy Support Library v4
    implementation("androidx.fragment:fragment-ktx:1.8.5") // Fragment KTX


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}