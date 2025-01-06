plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.openclassrooms.projet8vitesse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.openclassrooms.projet8vitesse"
        minSdk = 24
        targetSdk = 35
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
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Dépendances pour les tests instrumentés
    androidTestImplementation (libs.androidx.core)
    androidTestImplementation (libs.androidx.junit.v115)
    androidTestImplementation (libs.androidx.espresso.core.v351)

    // Pour coroutines test sur instrumentation
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.monitor)
    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-testing:2.6.1")


    //Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation (libs.converter.moshi)
    implementation(libs.converter.gson)


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.4")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

    //Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation(kotlin("script-runtime"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Java 8 Time (Instant)
    implementation(libs.jakewharton.threetenabp)

    // Moshi JSON Library
    implementation (libs.moshi)
    implementation (libs.moshi.kotlin)
    kapt (libs.moshi.kotlin.codegen)

}
kapt {
    correctErrorTypes = true
}