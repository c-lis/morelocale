package jp.co.c_lis.ccl.morelocale.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.ui.locale_list.LocaleListFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, LocaleListFragment.getInstance())
                    .commit()
        }
    }
}
