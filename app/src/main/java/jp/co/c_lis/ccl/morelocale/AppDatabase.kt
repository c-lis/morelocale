package jp.co.c_lis.ccl.morelocale

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.co.c_lis.ccl.morelocale.dao.LocaleItemDao
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem

@Database(entities = [LocaleItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun localeItemDao(): LocaleItemDao
}
