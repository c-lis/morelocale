package jp.co.c_lis.ccl.morelocale

import android.app.Application
import androidx.room.Room
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
class MainApplication : Application() {

    companion object {

        var db: AppDatabase? = null

        fun getDbInstance(application: Application): AppDatabase {
            db?.also {
                return it
            }

            return Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    BuildConfig.DATABASE_FILE_NAME
            ).build().also {
                db = it
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

    }
}
