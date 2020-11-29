package jp.co.c_lis.ccl.morelocale.ui.license

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import jp.co.c_lis.ccl.morelocale.R

class LicenseActivity : AppCompatActivity(R.layout.activity_license) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, LicenseFragment.getInstance())
                    .commit()
        }
    }
}
