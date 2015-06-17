package jp.co.c_lis.morelocale.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.settings.morelocale.LocaleItem;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.co.c_lis.ccl.morelocale.R;
import jp.co.c_lis.morelocale.MoreLocale;
import jp.co.c_lis.morelocale.event.SelectLocale;

public abstract class BaseListFragment extends Fragment {

    Realm mRealm;

    static Realm getRealmInstance(Context context) {
        return Realm.getInstance(context.getFilesDir(), "locales.realm");
    }

    private LocaleRecyclerViewAdapter mAdapter;
    private RealmResults<LocaleItem> mResult;

    public BaseListFragment() {
    }

    @InjectView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locale_list, container, false);

        ButterKnife.inject(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        mRealm = getRealmInstance(getActivity());
        mResult = mRealm.where(LocaleItem.class).findAllSorted("lastUsedDate", false);

        mAdapter = new LocaleRecyclerViewAdapter(getActivity(), mResult);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

        mRealm.close();
    }

    public void onEventAsync(SelectLocale event) {
        MoreLocale.setLocale(event.locale);
    }

    public void onEventMainThread(SelectLocale event) {
        mRealm.beginTransaction();
        LocaleItem item = mRealm.where(LocaleItem.class)
                .equalTo("language", event.locale.getLanguage())
                .equalTo("country", event.locale.getCountry())
                .findFirst();

        if (item != null) {
            item.setLastUsedDate(System.currentTimeMillis());
        }
        mRealm.commitTransaction();
    }

    void reload() {
        updateResult(mResult);
        mAdapter.notifyDataSetChanged();
    }

    abstract void updateResult(RealmResults<LocaleItem> result);

    public static class LocaleRecyclerViewAdapter
            extends RecyclerView.Adapter<LocaleRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private RealmResults<LocaleItem> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public LocaleRecyclerViewAdapter(Context context, RealmResults<LocaleItem> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.locale_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Locale locale = new Locale(mValues.get(position).getLanguage(), mValues.get(position).getCountry());
            holder.mBoundString = locale.getDisplayName();
            holder.mTextView.setText(locale.getDisplayName() + mValues.get(position).getLastUsedDate());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new SelectLocale(locale));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
