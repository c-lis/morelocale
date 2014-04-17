package com.android.settings.morelocale.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.settings.morelocale.lang.MoreLocale;
import com.android.settings.morelocale.util.ApplicationUtils;

import java.util.Locale;

import jp.co.c_lis.ccl.morelocale.R;

public class GlasswareMainActivity extends Activity {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocale";

    private TextView mLocaleView;

    private Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ApplicationUtils.checkPermission(this)) {
            setContentView(R.layout.glass_main);

            mLocaleView = (TextView) findViewById(R.id.tv_locale);
            mLocale = getResources().getConfiguration().locale;
            showLocale();
        } else {
            setContentView(R.layout.glass_main_permission_denied);
        }
    }

    private void showLocale() {
        String loc = new StringBuffer()
                .append(mLocale.getDisplayLanguage())
                .append(" / ")
                .append(mLocale.getDisplayCountry())
                .toString();
        mLocaleView.setText(loc);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.glassware_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent = new Intent(this, GlasswareSelectActivity.class);
        switch (item.getItemId()) {
            case R.id.main_menu_change_language:
                intent.putExtra(GlasswareSelectActivity.KEY_MODE, GlasswareSelectActivity.MODE_LANGUAGE);
                startActivityForResult(intent, R.id.main_menu_change_language);
                break;
            case R.id.main_menu_change_country:
                intent.putExtra(GlasswareSelectActivity.KEY_MODE, GlasswareSelectActivity.MODE_COUNTRY);
                startActivityForResult(intent, R.id.main_menu_change_country);
                break;
            case R.id.main_menu_about:
                Intent about = new Intent(this, GlasswareAboutActivity.class);
                startActivity(about);
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String title = data.getStringExtra(GlasswareSelectActivity.KEY_TITLE);
        String value = data.getStringExtra(GlasswareSelectActivity.KEY_VALUE);

        switch (requestCode) {
            case R.id.main_menu_change_language: {
                mLocale = new Locale(value, mLocale.getCountry());
                if (MoreLocale.setLocale(mLocale)) {
                    showLocale();
                }
                break;
            }
            case R.id.main_menu_change_country: {
                mLocale = new Locale(mLocale.getLanguage(), value);
                if (MoreLocale.setLocale(mLocale)) {
                    showLocale();
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (ApplicationUtils.checkPermission(this)) {
                openOptionsMenu();
            }
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}
