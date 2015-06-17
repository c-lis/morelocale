package jp.co.c_lis.morelocale.glass.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Locale;

import jp.co.c_lis.morelocale.MoreLocale;
import jp.co.c_lis.morelocale.glass.R;
import jp.co.c_lis.morelocale.utils.ApplicationUtils;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

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
        getMenuInflater().inflate(R.menu.glassware_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent = new Intent(this, SelectActivity.class);
        int id = item.getItemId();
        if(id ==  R.id.main_menu_change_language) {
            intent.putExtra(SelectActivity.KEY_MODE, SelectActivity.MODE_LANGUAGE);
            startActivityForResult(intent, R.id.main_menu_change_language);
        } else if(id == R.id.main_menu_change_country) {
            intent.putExtra(SelectActivity.KEY_MODE, SelectActivity.MODE_COUNTRY);
            startActivityForResult(intent, R.id.main_menu_change_country);
        } else if(id == R.id.main_menu_about) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String title = data.getStringExtra(SelectActivity.KEY_TITLE);
        String value = data.getStringExtra(SelectActivity.KEY_VALUE);

        if(requestCode == R.id.main_menu_change_language) {
            mLocale = new Locale(value, mLocale.getCountry());
            if (MoreLocale.setLocale(mLocale)) {
                showLocale();
            }
        } else if(requestCode == R.id.main_menu_change_country) {
            mLocale = new Locale(mLocale.getLanguage(), value);
            if (MoreLocale.setLocale(mLocale)) {
                showLocale();
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
