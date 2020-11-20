package jp.co.c_lis.ccl.morelocale.repository

import android.content.res.AssetManager
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocaleRepository {

    suspend fun getAll(assetManager: AssetManager) = withContext(Dispatchers.IO) {
        val locales = assetManager.locales.filterNotNull()
        Timber.d("locales size ${locales.size}")

        return@withContext locales.map { localeStr -> createLocale(localeStr) }
    }
}
