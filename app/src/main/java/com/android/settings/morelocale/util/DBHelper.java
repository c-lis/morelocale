package com.android.settings.morelocale.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * データベースの管理クラス
 *
 * @author Keiji Ariyama C-LIS CO., LTD.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * データベースのファイル名
     */
    public static final String FILE_NAME = "morelocale.db";

    /**
     * データベースのバージョン
     */
    public static final int VERSION = 1;

    /**
     * コンストラクタ
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * SQL LOCALESテーブルの作成
     */
    private static final String SQL_CREATE_TABLE_LOCALES
            = "CREATE TABLE locales (" +
            "_id INTEGER PRIMARY KEY, " +
            "label TEXT, " +
            "language TEXT, " +
            "country TEXT, " +
            "variant TEXT, " +
            "row_order INTEGER, " +
            "preset_flg INTEGER " +
            " );";

    /**
     * LOCALESテーブル
     */
    public static final String TABLE_LOCALES = "locales";

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルの作成
        db.execSQL(SQL_CREATE_TABLE_LOCALES);
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
