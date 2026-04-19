plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

// ── Signing helpers ────────────────────────────────────────────────────────────
// In CI/CD: KEYSTORE_PATH points to the decoded .jks file (decoded by the workflow);
// KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD are GitHub secrets.
// Locally: falls back to the Android debug keystore (zero config needed).
val ksPath:     String? = System.getenv("KEYSTORE_PATH")
val ksPassword: String  = System.getenv("KEYSTORE_PASSWORD") ?: "android"
val ksAlias:    String  = System.getenv("KEY_ALIAS")         ?: "androiddebugkey"
val ksKeyPwd:   String  = System.getenv("KEY_PASSWORD")      ?: "android"
val ksFile: java.io.File = if (ksPath != null) file(ksPath)
    else file("${System.getProperty("user.home")}/.android/debug.keystore")

android {
    namespace = "com.evodart.glyphdial"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.evodart.glyphdial"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile     = ksFile
            storePassword = ksPassword
            keyAlias      = ksAlias
            keyPassword   = ksKeyPwd
        }
    }

    buildTypes {
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            signingConfig     = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    
    // Compose UI
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Lifecycle
    implementation(libs.bundles.lifecycle)
    
    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Hilt DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    
    // Image Loading
    implementation(libs.coil.compose)
    
    // Phone Number Parsing
    implementation(libs.libphonenumber)
    
    // Permissions
    implementation(libs.accompanist.permissions)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}