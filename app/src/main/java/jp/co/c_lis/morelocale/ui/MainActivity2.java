package jp.co.c_lis.morelocale.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import jp.co.c_lis.ccl.morelocale.R;
import jp.co.c_lis.morelocale.LocaleItem;
import jp.co.c_lis.morelocale.event.EnterSelectionMode;
import jp.co.c_lis.morelocale.event.ExitSelectionMode;
import jp.co.c_lis.morelocale.event.LocaleAdded;
import jp.co.c_lis.morelocale.event.UpdateLocale;
import jp.co.c_lis.morelocale.util.Utils;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = MainActivity2.class.getSimpleName();

    public enum Mode {
        Normal,
        Select,
    }

    private Mode mMode = Mode.Normal;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    private LocaleItem mSelectedLocaleItem;

    @OnClick(R.id.fab)
    void onFabClick(View view) {
        if (mMode == Mode.Select) {
            return;
        }

        LocaleEditDialogFragment.getInstance().show(getSupportFragmentManager(), LocaleEditDialogFragment.class.getSimpleName());

        Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @InjectView(R.id.tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity2);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0 && mMode == Mode.Select) {
                    EventBus.getDefault().post(new ExitSelectionMode());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.main_menu_open_source:
                OpenSourceDialogFragment.getInstance()
                        .show(getSupportFragmentManager(), OpenSourceDialogFragment.class.getSimpleName());
                break;
            case R.id.main_menu_about:
                AboutDialogFragment.getInstance()
                        .show(getSupportFragmentManager(), AboutDialogFragment.class.getSimpleName());
                break;
            case R.id.main_menu_edit:
                LocaleEditDialogFragment.getInstance(mSelectedLocaleItem)
                        .show(getSupportFragmentManager(), AboutDialogFragment.class.getSimpleName());
                break;
            case android.R.id.home:
                EventBus.getDefault().post(new ExitSelectionMode());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new LocaleListFragment(), "Locales");
        adapter.addFragment(new RecentListFragment(), "Recents");
        viewPager.setAdapter(adapter);
    }

    public void onEventMainThread(EnterSelectionMode event) {
        mMode = Mode.Select;

        mSelectedLocaleItem = event.localeItem;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
        anim.setAnimationListener(animationListener);
        anim.setFillAfter(true);
        mFab.startAnimation(anim);

        mToolbar.setTitle(R.string.custom_locale_edit);
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.main_activity3);
    }

    public void onEventMainThread(ExitSelectionMode event) {
        mMode = Mode.Normal;

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        anim.setAnimationListener(animationListener);
        anim.setFillAfter(true);
        mFab.startAnimation(anim);

        mToolbar.setTitle(R.string.language_picker_title);
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.main_activity2);

        mSelectedLocaleItem = null;
    }

    public void onEventAsync(LocaleAdded event) {
        Log.d(TAG, "onEventAsync");

        Realm realm = Utils.getRealmInstance(this);
        LocaleItem item = realm
                .where(LocaleItem.class)
                .equalTo("language", event.item.getLanguage())
                .equalTo("country", event.item.getCountry())
                .findFirst();

        realm.beginTransaction();

        if (item == null) {
            item = realm.createObject(LocaleItem.class);
        }

        item.setLabel(event.item.getLabel());
        item.setHasLabel(true);
        item.setLanguage(event.item.getLanguage());
        item.setCountry(event.item.getCountry());
        item.setLastUsedDate(System.currentTimeMillis());

        realm.commitTransaction();

        EventBus.getDefault().post(new UpdateLocale());
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
