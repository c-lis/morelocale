package jp.co.c_lis.morelocale.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    @InjectView(R.id.about_tv_version)
    TextView mVersion;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.about, null);
        ButterKnife.inject(this, view);

        mVersion.setText(getString(R.string.version) + BuildConfig.VERSION_NAME);

        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.language_picker_title);
        dialog.setContentView(view);

        return dialog;
    }
}
