plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.ncfu.autoshow"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.ncfu.autoshow"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Базовый URL серверной части (порт 8081 — хост-порт backend в docker-compose).
        // Адрес зависит от того, ГДЕ запущено приложение:
        //   • эмулятор Android -> "http://10.0.2.2:8081/"        (10.0.2.2 = localhost ПК из эмулятора)
        //   • реальный телефон -> "http://<IP-ПК-в-Wi-Fi>:8081/" (телефон и ПК в одной Wi-Fi сети)
        // Сейчас выставлен IP компьютера для запуска на реальном телефоне.
        // Если IP компьютера сменится (новый сеанс Wi-Fi/DHCP) — поправьте здесь и пересоберите APK.
        buildConfigField("String", "BASE_URL", "\"http://10.206.136.112:8081/\"")

        vectorDrawables { useSupportLibrary = true }
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
}
