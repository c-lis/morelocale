package jp.co.c_lis.morelocale.glass.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import jp.co.c_lis.morelocale.glass.R;
import jp.co.c_lis.morelocale.utils.ApplicationUtils;

public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getSimpleName();

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
