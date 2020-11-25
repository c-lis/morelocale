package jp.co.c_lis.ccl.morelocale.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.R

class EditLocaleDialog : DialogFragment() {

    private enum class MODE(
            val titleRes: Int,
            val positiveButtonLabelRes: Int
    ) {
        ADD(R.string.add_locale, R.string.add),
        EDIT(R.string.edit_locale, R.string.save),
        SET(R.string.set_custom_locale, R.string.set),
    }

    companion object {
        val TAG = EditLocaleDialog::class.simpleName

        private const val KEY_MODE = "key_mode"

        fun getAddInstance(): EditLocaleDialog {
            val args = Bundle().apply {
                putInt(KEY_MODE, MODE.ADD.ordinal)
            }
            return EditLocaleDialog().also {
                it.arguments = args
            }
        }

        fun getEditInstance(): EditLocaleDialog {
            val args = Bundle().apply {
                putInt(KEY_MODE, MODE.EDIT.ordinal)
            }
            return EditLocaleDialog().also {
                it.arguments = args
            }
        }

        fun getSetInstance(): EditLocaleDialog {
            val args = Bundle().apply {
                putInt(KEY_MODE, MODE.SET.ordinal)
            }
            return EditLocaleDialog().also {
                it.arguments = args
            }
        }
    }

    private var mode = MODE.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = MODE.values()[requireArguments().getInt(KEY_MODE)]
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val contentView = layoutInflater.inflate(R.layout.dialog_edit_locale, null).also { view ->
        }

        return AlertDialog.Builder(requireContext())
                .setTitle(mode.titleRes)
                .setView(contentView)
                .setPositiveButton(mode.positiveButtonLabelRes) { _, _ ->
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .create()
    }
}
