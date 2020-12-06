package jp.co.c_lis.ccl.morelocale.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import jp.co.c_lis.ccl.morelocale.R

enum class Type(val titleRes: Int, val isoTitle: Int) {
    Iso639(R.string.language, R.string.iso639),
    Iso3166(R.string.country, R.string.iso3166),
}

class Converters {
    @TypeConverter
    fun fromString(value: String?) = when (value) {
        Type.Iso3166.name -> Type.Iso3166
        Type.Iso639.name -> Type.Iso639
        else -> null
    }

    @TypeConverter
    fun toString(type: Type?) = type?.name
}

@Entity
data class LocaleIsoItem(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        var type: Type? = null,
        val label: String,
        val value: String,
)
