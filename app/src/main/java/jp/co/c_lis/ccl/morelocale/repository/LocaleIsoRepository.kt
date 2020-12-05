package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import android.content.res.Resources
import jp.co.c_lis.ccl.morelocale.MainApplication
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class LocaleIsoRepository(application: Application) {

    private val db = MainApplication.getDbInstance(application)

    abstract val type: Type

    suspend fun findAll(resources: Resources) = withContext(Dispatchers.IO) {
        val list = db.localeIsoItemDao().findByType(type.name)
        if (list.isNotEmpty()) {
            return@withContext list
        }

        init(type, resources)

        return@withContext db.localeIsoItemDao().findByType(type.name)
    }

    private suspend fun init(type: Type, resources: Resources) = withContext(Dispatchers.Default) {
        when (type) {
            Type.Iso3166 -> {
                init(resources.getStringArray(R.array.iso_3166_title),
                        resources.getStringArray(R.array.iso_3166_value)
                )
            }
            Type.Iso639 -> {
                init(resources.getStringArray(R.array.iso_639_title),
                        resources.getStringArray(R.array.iso_639_value))
            }
        }
    }

    private suspend fun init(
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
                    isListed = true
            ))
        }
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
