plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cyzen.answer"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        applicationId = "cyzen.answer"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        signingConfig = signingConfigs.getByName("debug")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            vcsInfo.include = false //去除version-control-info.textproto
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources { //去除无用的文件
            excludes += "/META-INF/**"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin-tooling-metadata.json"
        }
    }
    dependenciesInfo {
        includeInApk = false
    }
}

dependencies {
    implementation("org.apache.poi:poi-ooxml:5.5.1")
}