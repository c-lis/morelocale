package jp.co.c_lis.ccl.morelocale.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Locale

@Entity
data class LocaleItem(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val country: String,
        val language: String? = null
) {

    @Ignore
    val locale: Locale = if (language != null) {
        Locale(language, country)
    } else {
        Locale("", country)
    }

    val label: String
        get() {
            return if (language != null) {
                "$country $language"
            } else {
                country
            }
        }

    val displayName: String
        get() = locale.displayName
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
