package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import android.content.res.AssetManager
import androidx.room.Room
import jp.co.c_lis.ccl.morelocale.AppDatabase
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocaleRepository(applicationContext: Context) {

    private val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, BuildConfig.DATABASE_FILE_NAME
    ).build()

    suspend fun getAll(assetManager: AssetManager) = withContext(Dispatchers.IO) {
        val localeList = db.localeItemDao()
                .findAll()
        if (localeList.isNotEmpty()) {
            return@withContext localeList
        }

        val localeItems = assetManager.locales
                .filterNotNull()
                .map { localeStr -> createLocale(localeStr) }
        db.localeItemDao()
                .insertAll(localeItems)

        return@withContext db.localeItemDao()
                .findAll()
    }
}
