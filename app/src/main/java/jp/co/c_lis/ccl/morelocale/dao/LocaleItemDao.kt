package jp.co.c_lis.ccl.morelocale.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem

@Dao
interface LocaleItemDao {

    @Query("SELECT * FROM localeItem ORDER BY id DESC")
    fun findAll(): List<LocaleItem>

    @Insert
    fun insertAll(localeItems: List<LocaleItem>)

    @Insert
    fun insert(localeItem: LocaleItem)

    @Update
    fun update(localeItem: LocaleItem): Int

    @Delete
    fun delete(localeItem: LocaleItem)
}
