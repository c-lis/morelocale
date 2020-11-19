package jp.co.c_lis.ccl.morelocale.repository

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocaleRepository {

    suspend fun getAll(assetManager: AssetManager) = withContext(Dispatchers.IO) {
        val locales = assetManager.locales.filterNotNull()
        Timber.d("locales size ${locales.size}")

        return@withContext locales.map { localeStr -> createLocale(localeStr) }
    }

    private fun createLocale(localeStr: String): LocaleItem {
        val localeTokens = localeStr.split("-")
        return when (localeTokens.size) {
            0 -> {
                LocaleItem(country = localeStr)
            }
            1 -> {
                LocaleItem(country = localeTokens[0])
            }
            else -> {
                LocaleItem(country = localeTokens[0], language = localeTokens[1])
            }
        }
    }

    data class LocaleItem(
            private var label: String? = null,
            val hasLabel: Boolean = false,
            val country: String,
            val language: String? = null
    )
}
