package jp.co.c_lis.ccl.morelocale.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jp.co.c_lis.ccl.morelocale.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, LocaleListFragment.getInstance())
                    .commit()
        }
    }
}
