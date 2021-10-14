plugins {
    id("com.android.library")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 3
        targetSdk = 30
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
