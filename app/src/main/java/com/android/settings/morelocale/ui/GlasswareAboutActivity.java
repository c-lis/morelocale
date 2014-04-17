package com.android.settings.morelocale.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import jp.co.c_lis.ccl.morelocale.R;

public class GlasswareAboutActivity extends Activity {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.glass_about);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            finish();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}
