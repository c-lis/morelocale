package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import android.content.res.AssetManager
import androidx.room.Room
import jp.co.c_lis.ccl.morelocale.AppDatabase
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocaleRepository(applicationContext: Context) {

    private val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, BuildConfig.DATABASE_FILE_NAME
    ).build()

    suspend fun getAll(assetManager: AssetManager) = withContext(Dispatchers.IO) {
        val localeList = findAll()
        if (localeList.isNotEmpty()) {
            return@withContext localeList
        }

        val localeItems = assetManager.locales
                .filterNotNull()
                .reversed()
                .map { localeStr -> createLocale(localeStr).also { it.isPreset = true } }
        db.localeItemDao()
                .insertAll(localeItems)

        return@withContext findAll()
    }

    suspend fun findAll() = withContext(Dispatchers.IO) {
        return@withContext db.localeItemDao().findAll()
    }

    suspend fun create(locale: LocaleItem) = withContext(Dispatchers.IO) {
        db.localeItemDao()
                .insert(locale)
    }

    suspend fun update(locale: LocaleItem) = withContext(Dispatchers.IO) {
        db.localeItemDao()
                .update(locale)
    }
}
