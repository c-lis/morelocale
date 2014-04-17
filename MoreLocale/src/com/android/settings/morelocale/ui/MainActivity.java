/*
 * Copyright (C) 2009-2010 C-LIS CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.morelocale.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import jp.co.c_lis.ccl.morelocale.R;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.morelocale.lang.MoreLocale.Loc;
import com.android.settings.morelocale.util.DBHelper;
import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

public class MainActivity extends Activity implements OnItemClickListener, OnMenuItemClickListener,
        OnItemLongClickListener, OnClickListener {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocale";

    private ListView mLvLocales = null;

    // ＤＢ操作クラス
    private SQLiteDatabase mDb = null;

    private CursorAdapter mAdapter = null;

    private Cursor mCursor = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        mTvCustomLocale = (TextView)findViewById(R.id.locale_header_tv_custom_locale);
        mTvCustomLocale.setOnClickListener(this);

        DBHelper helper = new DBHelper(this, DBHelper.FILE_NAME, null, DBHelper.VERSION);
        mDb = helper.getWritableDatabase();

        mCursor = mDb
                .query(DBHelper.TABLE_LOCALES, PROJECTION, null, null, null, null, "row_order desc");
        mAdapter = new Adapter(this, mCursor);

        mLvLocales = (ListView) findViewById(R.id.main_lv_locales);
        mLvLocales.setOnItemClickListener(this);
        mLvLocales.setAdapter(mAdapter);
        mLvLocales.setOnItemLongClickListener(this);

        mLvLocales.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.locale_view, menu);
                menu.findItem(R.id.locale_view_delete).setOnMenuItemClickListener(
                        new OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {

                                if(isCustomLocale(mSelectedRowId)) {

                                    // 削除確認ダイアログの初期化
                                    initConfirmDeleteDialog(mSelectedRowId);
                                    mConfirmDeleteDialog.show();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.preset_locale_cannot_be_deleted,
                                            Toast.LENGTH_LONG).show();
                                }
                                return false;
                            }
                        });
            }
        });

        // 現在の地域と言語
        Locale locale = getResources().getConfiguration().locale;
        Loc loc = new Loc(getText(R.string.current_locale).toString(), locale);

        // 現在の地域と言語を表示
        Bundle data = new Bundle();
        data.putSerializable(KEY_LOCALE_DATA, loc);

        Message msg = mHandler.obtainMessage();
        msg.what = HANDLE_UPDATE_LOCALE_HEADER;
        msg.setData(data);
        mHandler.sendMessage(msg);

        if(mCursor.getCount() == 0) new Thread(mInit).start();
    }

    /**
     * カスタムロケールか判定
     * @param id
     * @return
     */
    private boolean isCustomLocale(long id) {
        if (mDb.isOpen()) {
            Cursor cursor = mDb.query(DBHelper.TABLE_LOCALES, PROJECTION, "_id = ? AND preset_flg = ?", new String[]{String.valueOf(id), String.valueOf(PRESET_FLG_FALSE)}, null, null,
                    "row_order desc");
            if(cursor.getCount() > 0) return true;
        }
        return false;
    }

    private long mSelectedRowId = 0;

    /**
     * 削除確認ダイアログの初期化
     */
    private void initConfirmDeleteDialog(final long selectedRowId) {
        if (mConfirmDeleteDialog == null) {
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            mConfirmDeleteDialog = ab.create();
            mConfirmDeleteDialog.setTitle(R.string.delete);
            mConfirmDeleteDialog
                    .setMessage(getText(R.string.would_you_like_to_delete_this_custom_locale));
        }
        mConfirmDeleteDialog.setButton(getText(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int rows = mDb.delete(DBHelper.TABLE_LOCALES, "_id = ? AND preset_flg = ?",
                                new String[] { String.valueOf(selectedRowId),
                                        String.valueOf(PRESET_FLG_FALSE) });
                        if (rows > 0) {
                            Toast.makeText(MainActivity.this, R.string.locale_was_deleted,
                                    Toast.LENGTH_LONG).show();
                            reloadDb();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.locale_wasnt_deleted,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        mConfirmDeleteDialog.setButton2(getText(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG_FLG) Log.d(LOG_TAG, "onDestroy");
        mDb.close();
    }

    private volatile int mStatus = STATUS_NOT_SET;

    private static final int STATUS_NOT_SET = 0x0;
    private static final int STATUS_PROCESSING = 0x1;

    private static final int HANDLE_INIT_FINISH = 0x424332;
    private static final int HANDLE_UPDATE_LOCALE = 0x424334;
    private static final int HANDLE_UPDATE_LOCALE_HEADER = 0x424339;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Loc loc = null;
            switch (msg.what) {
                case HANDLE_INIT_FINISH:
                    reloadDb();
                    break;
                case HANDLE_UPDATE_LOCALE:
                    loc = (Loc) msg.getData().getSerializable(KEY_LOCALE_DATA);
                    setLocale(loc);
                    setLocaleHeader(loc);
                    reloadDb();
                    break;
                case HANDLE_UPDATE_LOCALE_HEADER:
                    loc = (Loc) msg.getData().getSerializable(KEY_LOCALE_DATA);
                    setLocaleHeader(loc);
                    break;
            }
            ;
        }
    };

    private void reloadDb() {
        if (mDb.isOpen()) {
            mCursor = mDb.query(DBHelper.TABLE_LOCALES, PROJECTION, null, null, null, null,
                    "row_order desc");
            mAdapter.changeCursor(mCursor);
            mAdapter.notifyDataSetChanged();
        }
    }

    public static final String[] PROJECTION = new String[] { "_id", "label", "language", "country",
            "variant", "preset_flg" };
    public static final int COLUMN_INDEX_LOCALES_ID = 0;
    public static final int COLUMN_INDEX_LOCALES_LABEL = 1;
    public static final int COLUMN_INDEX_LOCALES_LANGUAGE = 2;
    public static final int COLUMN_INDEX_LOCALES_COUNTRY = 3;
    public static final int COLUMN_INDEX_LOCALES_VARIANT = 4;
    public static final int COLUMN_INDEX_LOCALES_PRESETFLAG = 5;

    private class Adapter extends CursorAdapter {

        public Adapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = View.inflate(context, R.layout.main_item2, null);

            int presetFlg = cursor.getInt(COLUMN_INDEX_LOCALES_PRESETFLAG);
            int bgColor = getResources().getColor(R.color.blue);
            if (presetFlg == PRESET_FLG_FALSE) {
                bgColor = getResources().getColor(R.color.green);

                ((TextView) view.findViewById(R.id.label)).setText(cursor
                        .getString(COLUMN_INDEX_LOCALES_LABEL));
            } else {
                bgColor = getResources().getColor(R.color.blue);

                String language = cursor.getString(COLUMN_INDEX_LOCALES_LANGUAGE);
                String country = cursor.getString(COLUMN_INDEX_LOCALES_COUNTRY);
                String variant = cursor.getString(COLUMN_INDEX_LOCALES_VARIANT);
                Locale locale = new Locale(language, country, variant);
                ((TextView) view.findViewById(R.id.label)).setText(locale.getDisplayName());
            }

            ((TextView) view.findViewById(R.id.preset_flg)).setBackgroundColor(bgColor);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int presetFlg = cursor.getInt(COLUMN_INDEX_LOCALES_PRESETFLAG);
            int bgColor = getResources().getColor(R.color.blue);
            if (presetFlg == PRESET_FLG_FALSE) {
                bgColor = getResources().getColor(R.color.green);

                ((TextView) view.findViewById(R.id.label)).setText(cursor
                        .getString(COLUMN_INDEX_LOCALES_LABEL));
            } else {
                bgColor = getResources().getColor(R.color.blue);

                String language = cursor.getString(COLUMN_INDEX_LOCALES_LANGUAGE);
                String country = cursor.getString(COLUMN_INDEX_LOCALES_COUNTRY);
                String variant = cursor.getString(COLUMN_INDEX_LOCALES_VARIANT);
                Locale locale = new Locale(language, country, variant);
                ((TextView) view.findViewById(R.id.label)).setText(locale.getDisplayName());
            }

            ((TextView) view.findViewById(R.id.preset_flg)).setBackgroundColor(bgColor);

        }
    };

    private static final String KEY_LOCALE_DATA = "loc";

    /**
     * 初期化処理
     */
    private Runnable mInit = new Runnable() {
        public void run() {

            mStatus = STATUS_PROCESSING;

            // 端末プリセットのロケール情報を取得する
            List<Loc> locs = getPresetLocales();

            if (mDb.isOpen()) {
                int rows = mDb.delete(DBHelper.TABLE_LOCALES, "preset_flg = ?",
                        new String[] { String.valueOf(PRESET_FLG_TRUE) });
                if (DEBUG_FLG) Log.d(LOG_TAG, "row " + rows + "are deleted.");

                int rowOrder = 0;
                for (Loc l : locs) {
                    rowOrder += 5;

                    // DBへ追加
                    insertLocale(l, rowOrder, PRESET_FLG_TRUE);
                }
            }

            mStatus = STATUS_NOT_SET;

            mHandler.sendEmptyMessage(HANDLE_INIT_FINISH);
        }
    };

    private static final int PRESET_FLG_TRUE = 1;
    private static final int PRESET_FLG_FALSE = 0;

    /**
     * 
     * @param l
     */
    private void insertLocale(Loc l, long rowOrder, int presetFlg) {
        ContentValues values = new ContentValues();
        int id = l.hashCode();

        values.put("_id", id);
        values.put("label", l.label);
        values.put("language", l.locale.getLanguage());
        values.put("country", l.locale.getCountry());
        values.put("variant", l.locale.getVariant());
        values.put("row_order", rowOrder);
        values.put("preset_flg", presetFlg);

        if (DEBUG_FLG) Log.d(LOG_TAG, "row " + l.label);

        int rows = mDb.update(DBHelper.TABLE_LOCALES, values, "_id = ?", new String[] { String
                .valueOf(id) });
        if (DEBUG_FLG) Log.d(LOG_TAG, "row " + rows + " updated.");
        if (rows == 0) mDb.insert(DBHelper.TABLE_LOCALES, null, values);

    }

    /**
     * 先頭文字を大文字にする
     * 
     * @param s
     * @return 先頭が大文字になった文字列
     */
    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        mLvLocales.requestFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.main_menu_custom_locale).setOnMenuItemClickListener(this);
        menu.findItem(R.id.main_menu_about).setOnMenuItemClickListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ヘッダの表示を変更する
     * 
     * @param loc
     */
    private void setLocaleHeader(Loc loc) {
        String name = loc.locale.getDisplayName();
        String language = loc.locale.getLanguage();
        String country = loc.locale.getCountry();
        String variant = loc.locale.getVariant();

        String NA = "N/A";
        if (language.equals("")) language = NA;
        if (country.equals("")) country = NA;
        if (variant.equals("")) variant = NA;

        String value = language + " " + country + " " + variant;
        ((TextView) findViewById(R.id.locale_header_tv_locale_name)).setText(name);
        ((TextView) findViewById(R.id.locale_header_tv_locale_value)).setText(value);
    }

    private static final String PERMISSION_CHANGE_CONFIGURATION = "android.permission.CHANGE_CONFIGURATION";

    private boolean checkPermission() {
        return (PackageManager.PERMISSION_GRANTED
                == getPackageManager().checkPermission(PERMISSION_CHANGE_CONFIGURATION,
                getPackageName()));
    }

    /**
     * 言語を設定
     * 
     * @param loc
     */
    private void setLocale(Loc loc) {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();

            config.locale = loc.locale;
            config.userSetLocale = true;

            am.updateConfiguration(config);

        } catch (RemoteException e) {
            if (DEBUG_FLG) Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * リストのクリック
     */
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

        if (mStatus == STATUS_PROCESSING) return;

        mCursor.moveToPosition(position);

        String label = mCursor.getString(COLUMN_INDEX_LOCALES_LABEL);
        String language = mCursor.getString(COLUMN_INDEX_LOCALES_LANGUAGE);
        String country = mCursor.getString(COLUMN_INDEX_LOCALES_COUNTRY);
        String variant = mCursor.getString(COLUMN_INDEX_LOCALES_VARIANT);

        if (label == null) label = "";
        if (language == null) language = "";
        if (country == null) country = "";
        if (variant == null) variant = "";

        Loc loc = new Loc(label, new Locale(language, country, variant));
        changeLocale(loc);
    }

    private void changeLocale(Loc loc) {
        final Bundle data = new Bundle();
        data.putSerializable(KEY_LOCALE_DATA, loc);

        final Message msg = mHandler.obtainMessage();
        msg.what = HANDLE_UPDATE_LOCALE;
        msg.setData(data);

        final AlertDialog.Builder ab = new AlertDialog.Builder(this)
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_message);

        ab.setPositiveButton(R.string.show_how_to_use_pm_command, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ad = new AlertDialog.Builder(MainActivity.this).create();
                ad.setTitle(R.string.show_how_to_use_pm_command);
                ad.setMessage(getString(R.string.how_to_use_pm_command_message));
                ad.show();
            }
        });
        ab.setNegativeButton(R.string.use_superuser_privilege, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (getPermissionAsRoot()) {
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(MainActivity.this, R.string.could_not_get_su_privlege, Toast.LENGTH_LONG).show();
                }
            }
        });

        final Thread th = new Thread() {
            public void run() {

                if (checkPermission()) {
                    mHandler.sendMessage(msg);
                } else {
                    // CHANGE_CONFIGURATIONのパーミッションがない場合
                    mHandler.post(new Runnable() {
                        public void run() {
                            ab.create().show();
                        }
                    });
                }
            }
        };
        th.start();
    }

    private static final int TIMEOUT = 10 * 1000;

    private boolean getPermissionAsRoot() {
        if (DEBUG_FLG) {
            Log.d(LOG_TAG, "try grant permmission");
        }

        try {
            RootTools
                    .getShell(true)
                    .add(new CommandCapture(0,
                            "pm grant jp.co.c_lis.ccl.morelocale android.permission.CHANGE_CONFIGURATION"))
                    .waitForFinish(TIMEOUT);

            Log.d(LOG_TAG, "success grant permmission");
            return true;

        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "InterruptedException", e);
        } catch (TimeoutException e) {
            Log.e(LOG_TAG, "TimeoutException", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException", e);
        }

        return false;
    }

    private Dialog mCustomLocaleDialog = null;
    private EditText mLabel = null;
    private EditText mLanguage = null;
    private EditText mCountry = null;
    private EditText mVariant = null;

    private void showCustomLocaleSaveDialog() {
        showCustomLocaleDialog(true);
    }

    private void showCustomLocaleDialog() {
        showCustomLocaleDialog(false);
    }

    /**
     * カスタムロケールダイアログの表示
     */
    private void showCustomLocaleDialog(boolean saveMode) {
        if (mCustomLocaleDialog == null) {
            mCustomLocaleDialog = new Dialog(this);
        }

        mCustomLocaleDialog.setContentView(R.layout.custom_locale);

        mLabel = (EditText) mCustomLocaleDialog.findViewById(R.id.custom_locale_et_label);
        mLanguage = (EditText) mCustomLocaleDialog.findViewById(R.id.custom_locale_et_language);
        mCountry = (EditText) mCustomLocaleDialog.findViewById(R.id.custom_locale_et_country);
        mVariant = (EditText) mCustomLocaleDialog.findViewById(R.id.custom_locale_et_variant);
        final Button btnIso639 = (Button) mCustomLocaleDialog
                .findViewById(R.id.custom_locale_btn_639);
        final Button btnIso3166 = (Button) mCustomLocaleDialog
                .findViewById(R.id.custom_locale_btn_3166);

        Button setBtn = (Button)mCustomLocaleDialog.findViewById(R.id.custom_locale_btn_set);
        if (!saveMode) {
            mCustomLocaleDialog.setTitle(R.string.custom_locale);
            setBtn.setText(getText(R.string.set));
            setBtn.setOnClickListener(
                    new OnClickListener() {
                        public void onClick(View v) {
                            Locale locale = new Locale(mLanguage.getText().toString(), mCountry
                                    .getText().toString(), mVariant.getText().toString());
                            Loc loc = new Loc(getText(R.string.current_locale).toString(), locale);

                            changeLocale(loc);

                            if (mCustomLocaleDialog.isShowing()) mCustomLocaleDialog.dismiss();
                        }
                    });
        } else {
            mCustomLocaleDialog.setTitle(R.string.custom_locale_add);
            setBtn.setText(getText(R.string.add));
            setBtn.setOnClickListener(
                    new OnClickListener() {
                        public void onClick(View v) {
                            Locale locale = new Locale(mLanguage.getText().toString(), mCountry
                                    .getText().toString(), mVariant.getText().toString());
                            Loc loc = new Loc(mLabel.getText().toString(), locale);
                            insertLocale(loc, System.currentTimeMillis(), PRESET_FLG_FALSE);
                            reloadDb();
                            
                            if (mCustomLocaleDialog.isShowing()) mCustomLocaleDialog.dismiss();
                        }
                    });
        }

        mCustomLocaleDialog.findViewById(R.id.custom_locale_btn_cancel).setOnClickListener(
                new OnClickListener() {
                    public void onClick(View v) {
                        if (mCustomLocaleDialog.isShowing()) mCustomLocaleDialog.dismiss();
                    }
                });
        mCustomLocaleDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                mLabel.setText("");
                mLanguage.setText("");
                mCountry.setText("");
                mVariant.setText("");
            }
        });

        btnIso639.setOnClickListener(mCustomLocaleDialogOnClickListener);
        btnIso639.setOnCreateContextMenuListener(mCustomLocaleContextMenuListener);

        btnIso3166.setOnClickListener(mCustomLocaleDialogOnClickListener);
        btnIso3166.setOnCreateContextMenuListener(mCustomLocaleContextMenuListener);

        Locale nowLocale = getResources().getConfiguration().locale;
        mLanguage.setText(nowLocale.getLanguage());
        mCountry.setText(nowLocale.getCountry());
        mVariant.setText(nowLocale.getVariant());

        if (!saveMode) mCustomLocaleDialog.findViewById(R.id.custom_locale_tr_label).setVisibility(
                View.GONE);
        
        mCustomLocaleDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                mTvCustomLocale.setTextColor(getResources().getColor(R.color.green));
            }
        });

        mCustomLocaleDialog.show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.view.MenuItem.OnMenuItemClickListener#onMenuItemClick(android
     * .view.MenuItem)
     */
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_custom_locale:
                showCustomLocaleSaveDialog();
                return true;
            case R.id.main_menu_about:
                Dialog dialog = new Dialog(this);
                dialog.setTitle(getText(R.string.about));
                dialog.setContentView(R.layout.about);
                dialog.show();
                return true;
        }
        ;
        return false;
    }

    private AlertDialog mConfirmDeleteDialog = null;

    public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, final long id) {
        mSelectedRowId = id;
        return false;
    }

    private OnCreateContextMenuListener mCustomLocaleContextMenuListener = new OnCreateContextMenuListener() {

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            MenuInflater inflater = getMenuInflater();
            int len = 0;
            switch (v.getId()) {
                case R.id.custom_locale_btn_639:
                    inflater.inflate(R.menu.menu_639, menu);
                    menu.setHeaderTitle(R.string.ISO639);
                    len = menu.size();
                    for (int i = 0; i < len; i++) {
                        final int index = i;
                        menu.getItem(i).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                mLanguage.setText(getResources().getStringArray(
                                        R.array.iso_639_value)[index]);
                                return false;
                            }
                        });
                    }
                    break;

                case R.id.custom_locale_btn_3166:
                    inflater.inflate(R.menu.menu_3166, menu);
                    menu.setHeaderTitle(R.string.ISO3166);
                    len = menu.size();
                    for (int i = 0; i < len; i++) {
                        final int index = i;
                        menu.getItem(i).setOnMenuItemClickListener(new OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                mCountry.setText(getResources().getStringArray(
                                        R.array.iso_3166_value)[index]);
                                return false;
                            }
                        });
                    }
                    break;

            }
        }
    };

    private OnClickListener mCustomLocaleDialogOnClickListener = new OnClickListener() {
        
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.custom_locale_btn_639:
                    v.showContextMenu();
                    break;
                case R.id.custom_locale_btn_3166:
                    v.showContextMenu();
                    break;
            }
        }
    };

    private TextView mTvCustomLocale = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locale_header_tv_custom_locale:
                mTvCustomLocale.setTextColor(getResources().getColor(R.color.blue));
                showCustomLocaleDialog();
                break;
        }
    }

    /**
     * 端末プリセットのロケール情報を取得する
     * 
     * @return Locオブジェクトのリスト
     */
    private List<Loc> getPresetLocales() {

        // 返却する結果
        ArrayList<Loc> result = new ArrayList<Loc>();

        Hashtable<String, Loc> arr = new Hashtable<String, Loc>();

        // プリセットのロケール名の一覧を取得する
        String[] locales = getAssets().getLocales();
        Arrays.sort(locales);

        final int origSize = locales.length;

        for (int i = 0; i < origSize; i++) {
            String localeName = locales[i];

            if (localeName.equals("ja")) localeName = "ja_JP";

            int len = localeName.length();

            // 取得したロケールが2文字(ja)なら
            if (len == 2) {
                // ロケールオブジェクトの生成
                Locale tmpLocale = new Locale(localeName);
                arr.put(tmpLocale.getLanguage(), new Loc(
                        toTitleCase(tmpLocale.getDisplayLanguage()), tmpLocale));

                // 取得したロケールが5文字(ja_JP)なら
            } else if (len == 5) {

                // 先頭2文字は言語、4、5文字目は地域
                String language = localeName.substring(0, 2);
                String country = localeName.substring(3, 5);

                // ロケールオブジェクトの生成
                Locale tmpLocale = new Locale(language, country);

                // 現在のindexが0なら
                if (arr.size() == 0) {
                    // 先頭に入力
                    Loc tmp = new Loc(toTitleCase(tmpLocale.getDisplayLanguage()), tmpLocale);
                    arr.put(tmpLocale.getLanguage(), tmp);
                } else {

                    if (arr.containsKey(tmpLocale.getLanguage())) {
                        Loc prevLoc = arr.get(tmpLocale.getLanguage());

                        // 現在のインデックスより前のロケール情報と指定言語が同じであれば、
                        if (prevLoc.locale.getLanguage().equals(language)) {

                            // 現在のインデックスより前の地域情報の取得
                            String prevCountry = prevLoc.locale.getCountry();

                            // 地域が設定されていなければ、
                            if (prevCountry.length() == 0) {

                                // 既存のロケールを上書き
                                prevLoc.locale = tmpLocale;
                                prevLoc.label = toTitleCase(tmpLocale.getDisplayLanguage());

                            } else {

                                // 前のロケール列のラベルを変更
                                prevLoc.label = toTitleCase(prevLoc.locale.getDisplayName());

                                // 新しいロケールを設定
                                Loc tmp = new Loc(toTitleCase(tmpLocale.getDisplayName()),
                                        tmpLocale);
                                arr.put(localeName, tmp);
                            }

                        }

                        // 現在のインデックスより前のロケール情報と指定言語が異なる場合
                    } else {
                        String displayName;
                        if (localeName.equals("zz_ZZ")) {
                            displayName = "Pseudo...";
                        } else {
                            displayName = toTitleCase(tmpLocale.getDisplayLanguage());
                        }

                        // 先頭に入力
                        Loc tmp = new Loc(toTitleCase(displayName), tmpLocale);
                        arr.put(tmpLocale.getLanguage(), tmp);

                    }
                }
            }
        }

        result = new ArrayList<Loc>(arr.values());

        // 結果の返却
        return result;
    }
}
