package jp.co.c_lis.morelocale.ui;

import jp.co.c_lis.morelocale.LocaleItem;

import io.realm.RealmResults;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class RecentListFragment extends BaseListFragment {
    private static final String TAG = RecentListFragment.class.getSimpleName();

    public RecentListFragment() {
    }

    @Override
    RealmResults<LocaleItem> updateResult() {
        return mRealm
                .where(LocaleItem.class)
                .greaterThan("lastUsedDate", -1)
                .findAllSorted("lastUsedDate", false);
    }

}
