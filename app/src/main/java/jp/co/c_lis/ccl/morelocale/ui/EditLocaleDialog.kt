package jp.co.c_lis.ccl.morelocale.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.DialogEditLocaleBinding

class EditLocaleDialog : DialogFragment() {

    private enum class MODE(
            val titleRes: Int,
            val positiveButtonLabelRes: Int,
            val showLabelInput: Boolean,
    ) {
        ADD(R.string.add_locale, R.string.add, true),
        EDIT(R.string.edit_locale, R.string.save, true),
        SET(R.string.set_custom_locale, R.string.set, false),
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

    private var binding: DialogEditLocaleBinding? = null

    private var mode = MODE.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = MODE.values()[requireArguments().getInt(KEY_MODE)]
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val binding = DialogEditLocaleBinding.inflate(layoutInflater).also { binding ->
            binding.textInputLayoutLabel.visibility = if (mode.showLabelInput) {
                View.VISIBLE
            } else {
                View.GONE
            }
            this.binding = binding
        }

        return AlertDialog.Builder(requireContext())
                .setTitle(mode.titleRes)
                .setView(binding.root)
                .setPositiveButton(mode.positiveButtonLabelRes) { _, _ ->
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }
}
