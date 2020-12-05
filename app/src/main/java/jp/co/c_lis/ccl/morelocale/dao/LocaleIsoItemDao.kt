package jp.co.c_lis.ccl.morelocale.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem

@Dao
interface LocaleIsoItemDao {

    @Query("SELECT * FROM LocaleIsoItem ORDER BY id DESC")
    fun findAll(): List<LocaleIsoItem>

    @Query("SELECT * FROM LocaleIsoItem WHERE type = :type ORDER BY id DESC")
    fun findByType(type: String): List<LocaleIsoItem>

    @Query("SELECT * FROM LocaleIsoItem WHERE type = :type AND label LIKE :like ORDER BY id DESC")
    fun findMatchLabel(type: String, like: String): List<LocaleIsoItem>

    @Insert
    fun insertAll(localeIsoItemList: List<LocaleIsoItem>)

    @Insert
    fun insert(localeIsoItem: LocaleIsoItem)

    @Update
    fun update(localeIsoItem: LocaleIsoItem): Int

    @Delete
    fun delete(localeIsoItem: LocaleIsoItem)
}
