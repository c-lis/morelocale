package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import android.content.res.Resources
import jp.co.c_lis.ccl.morelocale.MainApplication
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class LocaleIsoRepository(applicationContext: Context) {

    private val db = MainApplication.getDbInstance(applicationContext)

    abstract val type: Type

    suspend fun findAll(resources: Resources) = withContext(Dispatchers.IO) {
        val list = db.localeIsoItemDao().findByType(type.name)
        if (list.isNotEmpty()) {
            return@withContext list
        }

        init(resources)

        return@withContext db.localeIsoItemDao().findByType(type.name)
    }

    abstract suspend fun init(resources: Resources)

    suspend fun init(
            titles: Array<String>,
            values: Array<String>
    ) = withContext(Dispatchers.IO) {
        titles.reverse()
        values.reverse()

        titles.forEachIndexed { index, title ->
            val value = values[index]
            add(LocaleIsoItem(
                    label = title,
                    value = value,
            ))
        }
    }

    suspend fun findMatchLabel(likeText: String) = withContext(Dispatchers.IO) {
        return@withContext db.localeIsoItemDao().findMatchLabel(type.name, likeText)
    }

    suspend fun add(localeIsoItem: LocaleIsoItem) = withContext(Dispatchers.IO) {
        db.localeIsoItemDao().insertAll(listOf(localeIsoItem.also { it.type = type }))
    }
}
