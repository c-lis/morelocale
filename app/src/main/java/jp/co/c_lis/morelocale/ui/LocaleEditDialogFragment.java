package jp.co.c_lis.morelocale.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import jp.co.c_lis.ccl.morelocale.R;
import jp.co.c_lis.morelocale.LocaleItem;
import jp.co.c_lis.morelocale.event.LocaleAdded;

/**
 * Created by keiji_ariyama on 6/20/15.
 */
public class LocaleEditDialogFragment extends DialogFragment {
    private static final String TAG = LocaleEditDialogFragment.class.getSimpleName();

    private static final String KEY_LOCALE = "locale";

    public static LocaleEditDialogFragment getInstance() {
        return getInstance(null);
    }

    public static LocaleEditDialogFragment getInstance(LocaleItem item) {
        LocaleEditDialogFragment fragment = new LocaleEditDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_LOCALE, item);

        fragment.setArguments(args);

        return fragment;
    }

    @InjectView(R.id.et_label)
    EditText mLabel;

    @InjectView(R.id.et_language)
    EditText mLanguage;

    @InjectView(R.id.et_country)
    EditText mCountry;

    @InjectView(R.id.et_variant)
    EditText mVariant;

    public LocaleEditDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.edit_locale, null);
        ButterKnife.inject(this, view);

        final LocaleItem item = (LocaleItem) getArguments().getSerializable(KEY_LOCALE);

        if (item != null) {
            mLabel.setText(item.getLabel());
            mCountry.setText(item.getCountry());
            mLanguage.setText(item.getLanguage());
        }

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab
                .setTitle(item != null ? R.string.custom_locale_edit : R.string.custom_locale_add)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocaleItem newItem = new LocaleItem();
                        newItem.setLabel(mLabel.getText().toString());
                        newItem.setHasLabel(true);
                        newItem.setLanguage(mLanguage.getText().toString());
                        newItem.setCountry(mCountry.getText().toString());
                        newItem.setLastUsedDate(System.currentTimeMillis());

                        EventBus.getDefault().post(new LocaleAdded(newItem));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return ab.create();
    }
}