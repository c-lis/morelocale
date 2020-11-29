package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferenceRepository(applicationContext: Context) {

    companion object {
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_COUNTRY = "key_country"
        private const val KEY_VARIANT = "key_variant"
    }

    private val pref = applicationContext.getSharedPreferences(
            BuildConfig.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    suspend fun saveLocale(localeItem: LocaleItem) = withContext(Dispatchers.IO) {
        pref.edit()
                .putString(KEY_LANGUAGE, localeItem.locale.language)
                .putString(KEY_COUNTRY, localeItem.locale.country)
                .putString(KEY_VARIANT, localeItem.locale.variant)
                .commit()
    }

    suspend fun loadLocale() = withContext(Dispatchers.IO) {
        val language = pref.getString(KEY_LANGUAGE, null) ?: return@withContext null
        val country = pref.getString(KEY_COUNTRY, null)
        val variant = pref.getString(KEY_VARIANT, null)

        return@withContext LocaleItem(
                language = language,
                country = country,
                variant = variant
        )
    }

    fun clearLocale() {
        pref.edit()
                .putString(KEY_LANGUAGE, null)
                .putString(KEY_COUNTRY, null)
                .putString(KEY_VARIANT, null)
                .apply()
    }

}
