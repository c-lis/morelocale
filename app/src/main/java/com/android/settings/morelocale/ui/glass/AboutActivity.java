package com.android.settings.morelocale.ui.glass;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.android.settings.morelocale.util.ApplicationUtils;

import jp.co.c_lis.ccl.morelocale.R;

public class AboutActivity extends Activity {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocale";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.glass_about);

        TextView versionName  = (TextView)findViewById(R.id.about_tv_version);
        versionName.setText("Version " + ApplicationUtils.getVersionName(this));
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
