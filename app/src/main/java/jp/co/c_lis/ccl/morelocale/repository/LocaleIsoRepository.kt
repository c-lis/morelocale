package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import jp.co.c_lis.ccl.morelocale.MainApplication
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class LocaleIsoRepository(application: Application) {

    private val db = MainApplication.getDbInstance(application)

    abstract val type: Type

    suspend fun findAll() = withContext(Dispatchers.IO) {
        return@withContext db.localeIsoItemDao().findByType(type.name)
    }

    suspend fun findMatchLabel(text: String?) = withContext(Dispatchers.IO) {
        if (text.isNullOrEmpty() || text.isBlank()) {
            return@withContext db.localeIsoItemDao().findByType(type.name)
        }

        return@withContext db.localeIsoItemDao().findMatchLabel(type.name, "%$text%")
    }

    suspend fun add(localeIsoItem: LocaleIsoItem) = withContext(Dispatchers.IO) {
        db.localeIsoItemDao().insertAll(listOf(localeIsoItem.also { it.type = type }))
    }

}
