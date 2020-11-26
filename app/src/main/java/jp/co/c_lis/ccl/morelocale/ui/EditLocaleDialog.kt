package jp.co.c_lis.ccl.morelocale.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.DialogEditLocaleBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import timber.log.Timber

class EditLocaleDialog : AppCompatDialogFragment() {

    enum class MODE(
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

        const val RESULT_KEY_LOCALE_ITEM = "result_key_locale_item"

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

    private fun createLocaleItem(): LocaleItem? {
        val binding = binding ?: return null

        val label = binding.inputLabel.text.let {
            if (it.isNullOrBlank()) {
                null
            } else {
                it.toString()
            }
        }
        val language = binding.inputLanguage.text.let {
            if (it.isNullOrBlank()) {
                null
            } else {
                it.toString()
            }
        }
        val country = binding.inputCountry.text.let {
            if (it.isNullOrBlank()) {
                ""
            } else {
                it.toString()
            }
        }
        val variant = binding.inputVariant.text.let {
            if (it.isNullOrBlank()) {
                null
            } else {
                it.toString()
            }
        }

        return LocaleItem(label = label, language = language, country = country, variant = variant)
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
                    val localeItem = createLocaleItem()
                    Timber.d("locale label = %s", localeItem?.label)
                    setFragmentResult(mode.name, bundleOf(RESULT_KEY_LOCALE_ITEM to localeItem))
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    // Do nothing
                }
                .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }
}
