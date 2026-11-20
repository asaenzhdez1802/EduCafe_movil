plugins {
    alias(libs.plugins.android.application)
    kotlin("plugin.serialization") version "2.2.10"
}

android {
    namespace = "com.example.educafe"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.educafe"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // 1. Convertir los datos JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // 2. Consumir las API con Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // 3. Peticiones HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 4. Cargar imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 5. Listas y tarjetas
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}