package jp.co.c_lis.ccl.morelocale.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Locale

@Entity
data class LocaleItem(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val label: String? = null,
        val language: String? = null,
        val country: String? = null,
        val variant: String? = null,
        var isPreset: Boolean = false,
) : Parcelable {

    @Ignore
    val locale: Locale = if (language != null && country != null && variant != null) {
        Locale(language, country, variant)
    } else if (language != null && country != null && variant == null) {
        Locale(language, country)
    } else if (language != null && country == null && variant == null) {
        Locale(language)
    } else {
        Locale("")
    }

    val displayName: String
        get() = label ?: locale.displayName

    val displayFull: String
        get() {
            val language = language ?: "N/A"
            val variant = variant ?: "N/A"
            return "$displayName $language $country $variant"
        }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeString(language)
        parcel.writeString(country)
        parcel.writeString(variant)
        parcel.writeByte(if (isPreset) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocaleItem> {
        override fun createFromParcel(parcel: Parcel): LocaleItem {
            return LocaleItem(parcel)
        }

        override fun newArray(size: Int): Array<LocaleItem?> {
            return arrayOfNulls(size)
        }
    }
}

fun createLocale(localeStr: String): LocaleItem {
    val localeTokens = localeStr.split("-")
    return when (localeTokens.size) {
        0 -> {
            LocaleItem(language = localeStr)
        }
        1 -> {
            LocaleItem(language = localeTokens[0])
        }
        2 -> {
            LocaleItem(
                    language = localeTokens[0],
                    country = localeTokens[1]
            )
        }
        else -> {
            LocaleItem(
                    language = localeTokens[0],
                    country = localeTokens[1],
                    variant = localeTokens[2]
            )
        }
    }
}

fun createLocale(locale: Locale): LocaleItem {
    return LocaleItem(
            language = locale.language,
            country = locale.country,
            variant = locale.variant
    )
}
