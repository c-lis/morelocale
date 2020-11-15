package jp.co.c_lis.morelocale.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.co.c_lis.ccl.morelocale.BuildConfig;
import jp.co.c_lis.ccl.morelocale.R;

/**
 * Created by keiji_ariyama on 6/18/15.
 */
public class AboutDialogFragment extends DialogFragment {

    public static AboutDialogFragment getInstance() {
        return new AboutDialogFragment();
    }

    public AboutDialogFragment() {
    }

    @BindView(R.id.about_tv_version)
    TextView mVersion;

    private Unbinder mBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.about, null);
        mBinding = ButterKnife.bind(this, view);

        mVersion.setText(String.format(Locale.US, "%s %s", getString(R.string.version), BuildConfig.VERSION_NAME));

        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.language_picker_title);
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding.unbind();
    }
}
