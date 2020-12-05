package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import android.content.res.AssetManager
import jp.co.c_lis.ccl.morelocale.MainApplication
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocaleRepository(application: Application) {

    private val db = MainApplication.getDbInstance(application)

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

    suspend fun delete(locale: LocaleItem) = withContext(Dispatchers.IO) {
        db.localeItemDao()
                .delete(locale)
    }
}
