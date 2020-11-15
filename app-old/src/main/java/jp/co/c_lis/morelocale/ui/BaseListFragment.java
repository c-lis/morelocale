package jp.co.c_lis.morelocale.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.co.c_lis.ccl.morelocale.R;
import jp.co.c_lis.morelocale.LocaleItem;
import jp.co.c_lis.morelocale.lib.MoreLocale;
import jp.co.c_lis.morelocale.event.EnterSelectionMode;
import jp.co.c_lis.morelocale.event.ExitSelectionMode;
import jp.co.c_lis.morelocale.event.SetLocale;
import jp.co.c_lis.morelocale.event.UpdateLocale;
import jp.co.c_lis.morelocale.util.Utils;

public abstract class BaseListFragment extends Fragment {
    private static final String TAG = BaseListFragment.class.getSimpleName();

    Realm mRealm;

    private LocaleRecyclerViewAdapter mAdapter;
    private RealmResults<LocaleItem> mResult;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mBinding;

    public BaseListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locale_list, container, false);

        mBinding = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mBinding.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        mRealm = Utils.getRealmInstance(getActivity());
        mResult = mRealm.where(LocaleItem.class)
                .greaterThan("lastUsedDate", 0)
                .findAll()
                .sort("lastUsedDate", Sort.ASCENDING);

        mAdapter = new LocaleRecyclerViewAdapter(getActivity(), mResult);
        mRecyclerView.setAdapter(mAdapter);

        reload();
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

        mRealm.close();
        mRealm = null;
    }

    public void onEventMainThread(UpdateLocale event) {
        reload();
    }

    public void onEventAsync(SetLocale event) {
        MoreLocale.setLocale(event.locale);
    }

    public void onEventMainThread(SetLocale event) {
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

    public void onEventMainThread(ExitSelectionMode event) {
        mAdapter.exitSelectionMode(false);
    }

    void reload() {
        mResult = updateResult();
        mAdapter.setResult(mResult);
        mAdapter.notifyDataSetChanged();
    }

    abstract RealmResults<LocaleItem> updateResult();

    public static class LocaleRecyclerViewAdapter
            extends RecyclerView.Adapter<LocaleRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private RealmResults<LocaleItem> mValues;

        private LocaleItem mSelectedItem;

        public void setResult(RealmResults<LocaleItem> result) {
            this.mValues = result;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView mTextView;
            public final ImageView mIcon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mIcon = (ImageView) view.findViewById(R.id.icon);
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

        void enterSelectionMode(LocaleItem localeItem) {
            mSelectedItem = localeItem;
            notifyDataSetChanged();

            EventBus.getDefault().post(new EnterSelectionMode(mSelectedItem));
        }

        void exitSelectionMode(boolean postEvent) {
            if (postEvent) {
                EventBus.getDefault().post(new ExitSelectionMode());
            }
            mSelectedItem = null;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.locale_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Locale locale = new Locale(mValues.get(position).getLanguage(), mValues.get(position).getCountry());

            String label = mValues.get(position).getLabel();
            if (!mValues.get(position).isHasLabel()) {
                label = TextUtils.isEmpty(mValues.get(position).getLabel()) ? locale.getDisplayName() : mValues.get(position).getLabel();
            }

            holder.mBoundString = label;
            holder.mTextView.setText(label);
            if (mSelectedItem != null && Utils.equals(mSelectedItem, mValues.get(position))) {
                holder.mIcon.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                holder.mIcon.setImageResource(R.drawable.ic_language_black_24dp);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitSelectionMode(true);

                    EventBus.getDefault().post(new SetLocale(locale));
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    enterSelectionMode(mValues.get(position));
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
