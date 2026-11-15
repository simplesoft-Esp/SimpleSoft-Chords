plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android {
 namespace = "com.simplesoft.chords"
 compileSdk = 36
 defaultConfig { applicationId = "com.simplesoft.chords"; minSdk = 26; targetSdk = 36; versionCode = 2; versionName = "0.2.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 buildFeatures { compose = true; buildConfig = true }
 sourceSets["main"].assets.srcDir("../shared")
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
 implementation(platform("androidx.compose:compose-bom:2025.10.01"))
 implementation("androidx.activity:activity-compose:1.11.0")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
 testImplementation("junit:junit:4.13.2")
 testImplementation("org.json:json:20240303")
}
