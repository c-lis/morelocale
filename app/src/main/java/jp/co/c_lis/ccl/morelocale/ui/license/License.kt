package jp.co.c_lis.ccl.morelocale.ui.license

data class License(
        val name: String,
        val url: String,
        val licensePathOnAsset: String
)

val LICENSES = listOf(
        License(
                "ic_menu_3d_globe.png",
                "https://android.googlesource.com/platform/packages/apps/Settings/",
                "license/aosp_creative_commons.txt"
        ),
        License(
                "Android Settings app",
                "https://source.android.com/",
                "license/aosp.txt"
        ),
        License(
                "AndroidX Jetpack - lifecycle ViewModel",
                "https://developer.android.com/jetpack/androidx/releases/lifecycle",
                "license/aosp_jetpack.txt"
        ),
        License(
                "AndroidX Jetpack - lifecycle LiveData",
                "https://developer.android.com/jetpack/androidx/releases/lifecycle",
                "license/aosp_jetpack.txt"
        ),
        License(
                "AndroidX Jetpack - Room Persistence Library",
                "https://developer.android.com/jetpack/androidx/releases/room",
                "license/aosp_jetpack.txt"
        ),
        License(
                "AndroidX Jetpack - Fragment",
                "https://developer.android.com/jetpack/androidx/releases/fragment",
                "license/aosp_jetpack.txt"
        ),
        License(
                "AndroidX Jetpack - RecyclerView",
                "https://developer.android.com/jetpack/androidx/releases/recyclerview",
                "license/aosp_jetpack.txt"
        ),
        License(
                "AndroidX Jetpack - Constraintlayout",
                "https://develoer.android.com/jetpack/androidx/releases/constraintlayout",
                "license/aosp_jetpack.txt"
        ),
        License(
                "Material Components for Android",
                "https://github.com/material-components/material-components-android",
                "license/google_material_design.txt"
        ),
        License(
                "Timber",
                "https://github.com/JakeWharton/timber",
                "license/timber.txt"
        ),
        License(
                "Kotlin Programming Language",
                "https://github.com/JetBrains/kotlin",
                "license/kotlin.txt"
        ),
        License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "license/kotlin-coroutines.txt"
        ),
)
