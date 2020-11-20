package jp.co.c_lis.ccl.morelocale.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem

@Dao
interface LocaleItemDao {

    @Query("SELECT * FROM localeItem")
    fun findAll(): List<LocaleItem>

    @Insert
    fun insertAll(localeItems: List<LocaleItem>)

    @Delete
    fun delete(localeItem: LocaleItem)
}
