package jp.co.c_lis.morelocale.ui;

import com.android.settings.morelocale.LocaleItem;

import io.realm.RealmResults;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class RecentListFragment extends BaseListFragment {

    public RecentListFragment() {
    }

    @Override
    void updateResult(RealmResults<LocaleItem> result) {
        result.where().greaterThan("lastUsedDate", -1);
    }

}
