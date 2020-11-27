package jp.co.c_lis.ccl.morelocale.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
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
        private const val KEY_LOCALE_ITEM = "key_locale_item"

        fun getAddInstance(): EditLocaleDialog {
            val args = Bundle().apply {
                putInt(KEY_MODE, MODE.ADD.ordinal)
            }
            return EditLocaleDialog().also {
                it.arguments = args
            }
        }

        fun getEditInstance(localeItem: LocaleItem): EditLocaleDialog {
            val args = Bundle().apply {
                putInt(KEY_MODE, MODE.EDIT.ordinal)
                putParcelable(KEY_LOCALE_ITEM, localeItem)
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

    private fun validation(): Boolean {
        val binding = binding ?: return false

        if (mode != MODE.SET && binding.inputLabel.text.isNullOrEmpty()) {
            binding.textInputLayoutLabel.error = getString(R.string.required)
            return false
        }

        return true
    }

    private fun createLocaleItem(editItem: LocaleItem?): LocaleItem? {
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

        return LocaleItem(
                id = editItem?.id ?: 0,
                label = label,
                language = language,
                country = country,
                variant = variant
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setFragmentResultListener(LocaleSelectorDialog.LocaleType.Iso639.name) { _, bundle ->
            val iso639Str = bundle.getString(LocaleSelectorDialog.RESULT_KEY_LOCALE)
                    ?: return@setFragmentResultListener
            binding?.inputLanguage?.setText(iso639Str)
        }
        setFragmentResultListener(LocaleSelectorDialog.LocaleType.Iso3166.name) { _, bundle ->
            val iso3166Str = bundle.getString(LocaleSelectorDialog.RESULT_KEY_LOCALE)
                    ?: return@setFragmentResultListener
            binding?.inputCountry?.setText(iso3166Str)
        }

        val editItem = requireArguments().getParcelable<LocaleItem>(KEY_LOCALE_ITEM)
        val binding = DialogEditLocaleBinding.inflate(layoutInflater).also { binding ->
            binding.textInputLayoutLabel.visibility = if (mode.showLabelInput) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.buttonIso639.setOnClickListener {
                LocaleSelectorDialog.getIso639Instance()
                        .show(parentFragmentManager, LocaleSelectorDialog.TAG)
            }
            binding.buttonIso3166.setOnClickListener {
                LocaleSelectorDialog.getIso3166Instance()
                        .show(parentFragmentManager, LocaleSelectorDialog.TAG)
            }

            editItem?.also { localeItem ->
                binding.inputLabel.setText(localeItem.label)
                binding.inputLanguage.setText(localeItem.language)
                binding.inputCountry.setText(localeItem.country)
                binding.inputVariant.setText(localeItem.variant)
                localeItem.id
            }

            this.binding = binding
        }

        return AlertDialog.Builder(requireContext())
                .setTitle(mode.titleRes)
                .setView(binding.root)
                .setPositiveButton(mode.positiveButtonLabelRes) { _, _ ->
                    // Do nothing
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    // Do nothing
                }
                .create()
    }

    override fun onResume() {
        super.onResume()

        val editItem = requireArguments().getParcelable<LocaleItem>(KEY_LOCALE_ITEM)

        val dialog = dialog
        if (dialog is AlertDialog) {
            // Prevent to close a dialog automatically.
            // https://stackoverflow.com/a/15619098
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!validation()) {
                    return@setOnClickListener
                }
                createLocaleItem(editItem)?.also { localeItem ->
                    Timber.d("locale label = %s", localeItem.label)
                    setFragmentResult(mode.name, bundleOf(RESULT_KEY_LOCALE_ITEM to localeItem))
                }
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }
}
