package jp.co.c_lis.ccl.morelocale.entity

import java.util.Locale

data class LocaleItem(
        val country: String,
        val language: String? = null
) {
    val label: String
        get() {
            return if (language != null) {
                "$country $language"
            } else {
                country
            }
        }

}

fun createLocale(localeStr: String): LocaleItem {
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

fun createLocale(locale: Locale): LocaleItem {
    return LocaleItem(country = locale.country, language = locale.language)
}
