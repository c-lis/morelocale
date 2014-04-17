package com.android.settings.morelocale.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import jp.co.c_lis.ccl.morelocale.R;

public class GlasswareSelectActivity extends Activity implements OnLocaleSelectedListener {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocale";

    public static final String KEY_MODE = "mode";
    public static final String KEY_TITLE = "title";
    public static final String KEY_VALUE = "value";

    public static final int MODE_LANGUAGE = 0x1;
    public static final int MODE_COUNTRY = 0x2;

    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMode = getIntent().getIntExtra(KEY_MODE, MODE_LANGUAGE);
    }

    private boolean mFlg = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!mFlg) {
            openOptionsMenu();
            mFlg = true;
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        finish();
    }

    @Override
    public void onLocaleSelected(String title, String value) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_VALUE, value);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setMenuItems(int titleArrayId, int valueArrayId, Menu menu, final OnLocaleSelectedListener listener) {
        String[] titles = getResources().getStringArray(titleArrayId);
        final String[] values = getResources().getStringArray(valueArrayId);

        int index = 0;
        for (final String title : titles) {
            MenuItem item = menu.add(title);
            final int idx = index;
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    listener.onLocaleSelected(title, values[idx]);
                    return false;
                }
            });
            index++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);

        switch (mMode) {
            case MODE_LANGUAGE:
                setMenuItems(R.array.iso_639_title, R.array.iso_639_value, menu, this);
                break;
            case MODE_COUNTRY:
                setMenuItems(R.array.iso_3166_title, R.array.iso_3166_value, menu, this);
                break;
        }


        return super.onCreateOptionsMenu(menu);
    }
}
