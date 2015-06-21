package jp.co.c_lis.morelocale.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.util.List;

import io.realm.RealmResults;
import jp.co.c_lis.morelocale.LocaleItem;
import jp.co.c_lis.morelocale.MoreLocale;
import jp.co.c_lis.morelocale.util.Utils;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class LocaleListFragment extends BaseListFragment {
    private static final String TAG = LocaleListFragment.class.getSimpleName();

    private final LoaderManager.LoaderCallbacks<List<MoreLocale.Loc>> mPresetLocaleInitializeLoaderCallback = new LoaderManager.LoaderCallbacks<List<MoreLocale.Loc>>() {

        @Override
        public Loader<List<MoreLocale.Loc>> onCreateLoader(int id, Bundle args) {
            PresetLocaleInitializeLoader loader = new PresetLocaleInitializeLoader(getActivity());
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<MoreLocale.Loc>> loader, List<MoreLocale.Loc> data) {
            mRealm.beginTransaction();
            for (MoreLocale.Loc loc : data) {
                LocaleItem item = mRealm.createObject(LocaleItem.class);
                item.setHasLabel(false);
                item.setLanguage(loc.locale.getLanguage());
                item.setCountry(loc.locale.getCountry());
                item.setLastUsedDate(-1);
            }
            mRealm.commitTransaction();

            reload();
        }

        @Override
        public void onLoaderReset(Loader<List<MoreLocale.Loc>> loader) {
            // do nothing
        }
    };

    public static class PresetLocaleInitializeLoader extends AsyncTaskLoader<List<MoreLocale.Loc>> {

        public PresetLocaleInitializeLoader(Context context) {
            super(context);
        }

        @Override
        public List<MoreLocale.Loc> loadInBackground() {
            return Utils.getPresetLocales(getContext().getAssets());
        }

    }

    public LocaleListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRealm.where(LocaleItem.class).findFirst() == null) {
            getLoaderManager().initLoader(0, new Bundle(), mPresetLocaleInitializeLoaderCallback);
        }
    }

    @Override
    RealmResults<LocaleItem> updateResult() {
        return mRealm
                .where(LocaleItem.class)
                .findAllSorted("lastUsedDate", false);
    }
}
