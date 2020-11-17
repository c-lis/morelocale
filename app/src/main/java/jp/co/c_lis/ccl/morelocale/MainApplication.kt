package jp.co.c_lis.ccl.morelocale

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

    }
}
