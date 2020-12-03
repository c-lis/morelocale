package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import androidx.room.Room
import jp.co.c_lis.ccl.morelocale.AppDatabase
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class LocaleIsoRepository(applicationContext: Context) {

    private val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, BuildConfig.DATABASE_FILE_NAME
    ).build()

    abstract val type: Type

    suspend fun findAll() = withContext(Dispatchers.IO) {
        return@withContext db.localeIsoItemDao().findByType(type.name)
    }

    suspend fun findMatchLabel(text: String) = withContext(Dispatchers.IO) {

    }

    suspend fun add(localeIsoItem: LocaleIsoItem) = withContext(Dispatchers.IO) {
        db.localeIsoItemDao().insertAll(listOf(localeIsoItem.also { it.type = type }))
    }

}
