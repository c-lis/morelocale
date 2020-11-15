package jp.co.c_lis.morelocale.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.co.c_lis.ccl.morelocale.R;

/**
 * Created by keiji_ariyama on 6/18/15.
 */
public class OpenSourceDialogFragment extends DialogFragment {
    private static final String TAG = OpenSourceDialogFragment.class.getSimpleName();

    private final LoaderManager.LoaderCallbacks<Void> mLoaderCallback = new LoaderManager.LoaderCallbacks<Void>() {
        @Override
        public Loader<Void> onCreateLoader(int id, Bundle args) {
            LicenseLoader loader = new LicenseLoader(getActivity());
            loader.forceLoad();
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<Void> loader, Void data) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Void> loader) {
        }
    };

    public static OpenSourceDialogFragment getInstance() {
        return new OpenSourceDialogFragment();
    }

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private OpenSourceRecyclerViewAdapter mAdapter;

    public OpenSourceDialogFragment() {
    }

    private Unbinder mBinding;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.open_source, null);
        mBinding = ButterKnife.bind(this, view);

        mAdapter = new OpenSourceRecyclerViewAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(mAdapter);

        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.open_source);
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getLoaderManager().initLoader(0, new Bundle(), mLoaderCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding.unbind();
    }

    public enum OssLibrary {
        CreativeCommons("Android Open Source Project", "license_creative_commons.txt"),
        AOSP("Android Open Source Project", "license_aosp.txt"),
        ButterKnife("ButterKnife", "license_butterknife.txt"),
        EventBus("EventBus", "license_eventbus.txt"),
        Realm("Realm", "license_realm.txt");

        public final String name;
        public final String fileName;
        public String license;

        OssLibrary(String name, String file) {
            this.name = name;
            fileName = file;
        }
    }

    public static class LicenseLoader extends AsyncTaskLoader<Void> {

        public LicenseLoader(Context context) {
            super(context);
        }

        @Override
        public Void loadInBackground() {
            for (OssLibrary ossLibrary : OssLibrary.values()) {
                load(ossLibrary);
            }
            return null;
        }

        private void load(OssLibrary ossLibrary) {
            InputStream is = null;
            try {
                is = getContext().getAssets().open(ossLibrary.fileName);
                ossLibrary.license = IOUtils.toString(is);
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public static class OpenSourceRecyclerViewAdapter
            extends RecyclerView.Adapter<OpenSourceRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;

        public OpenSourceRecyclerViewAdapter(Context context) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.oss_card, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final OpenSourceRecyclerViewAdapter.ViewHolder holder, int position) {
            OssLibrary value = OssLibrary.values()[position];
            holder.mBoundString = value.name;
            holder.mName.setText(value.name);
            holder.mLicense.setText(value.license);
        }

        @Override
        public int getItemCount() {
            return OssLibrary.values().length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final CardView mCardView;
            public final TextView mName;
            public final TextView mLicense;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mCardView = (CardView) view.findViewById(R.id.card_view);
                mName = (TextView) view.findViewById(R.id.tv_name);
                mLicense = (TextView) view.findViewById(R.id.tv_license);
            }
        }

    }
}
