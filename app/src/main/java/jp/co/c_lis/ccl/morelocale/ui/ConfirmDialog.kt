package jp.co.c_lis.ccl.morelocale.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import jp.co.c_lis.ccl.morelocale.R

class ConfirmDialog : AppCompatDialogFragment() {

    companion object {
        val TAG = ConfirmDialog::class.simpleName

        private const val REQUEST_KEY = "request_confirm_dialog"
        private const val RESULT_CLICKED_BUTTON = "result_clicked_button"

        private const val KEY_TITLE = "key_title"
        private const val KEY_MESSAGE = "key_message"
        private const val KEY_POSITIVE_BUTTON_LABEL = "key_positive_button_label"
        private const val KEY_NEGATIVE_BUTTON_LABEL = "key_negative_button_label"
        private const val KEY_NEUTRAL_BUTTON_LABEL = "key_neutral_button_label"

        fun show(
                targetFragment: Fragment,
                title: String,
                message: String,
                positiveButtonLabel: String? = null,
                negativeButtonLabel: String? = null,
                neutralButtonLabel: String? = null,
                onPositiveButtonClicked: (() -> Unit)? = null,
                onNegativeButtonClicked: (() -> Unit)? = null,
                onNeutralButtonClicked: (() -> Unit)? = null,
        ) {
            val args = Bundle().apply {
                putString(KEY_TITLE, title)
                putString(KEY_MESSAGE, message)
                putString(KEY_POSITIVE_BUTTON_LABEL, positiveButtonLabel)
                putString(KEY_NEGATIVE_BUTTON_LABEL, negativeButtonLabel)
                putString(KEY_NEUTRAL_BUTTON_LABEL, neutralButtonLabel)
            }
            val dialog = ConfirmDialog().also {
                it.arguments = args
            }
            targetFragment.childFragmentManager.setFragmentResultListener(
                    REQUEST_KEY, targetFragment.viewLifecycleOwner) { _, bundle ->
                when (bundle.getInt(RESULT_CLICKED_BUTTON)) {
                    AlertDialog.BUTTON_POSITIVE -> onPositiveButtonClicked?.invoke()
                    AlertDialog.BUTTON_NEGATIVE -> onNegativeButtonClicked?.invoke()
                    AlertDialog.BUTTON_NEUTRAL -> onNeutralButtonClicked?.invoke()
                }
            }
            dialog.show(targetFragment.childFragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val title = requireArguments().getString(KEY_TITLE)
        val message = requireArguments().getString(KEY_MESSAGE)
        val positiveButtonLabel = arguments?.getString(KEY_POSITIVE_BUTTON_LABEL)
        val negativeButtonLabel = arguments?.getString(KEY_NEGATIVE_BUTTON_LABEL)
        val neutralButtonLabel = arguments?.getString(KEY_NEUTRAL_BUTTON_LABEL)

        val builder = AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)

        positiveButtonLabel?.also {
            builder.setPositiveButton(it) { _: DialogInterface, _: Int ->
                setFragmentResult(REQUEST_KEY,
                        bundleOf(RESULT_CLICKED_BUTTON to AlertDialog.BUTTON_POSITIVE))
            }
        }
        negativeButtonLabel?.also {
            builder.setNegativeButton(it) { _: DialogInterface, _: Int ->
                setFragmentResult(REQUEST_KEY,
                        bundleOf(RESULT_CLICKED_BUTTON to AlertDialog.BUTTON_NEGATIVE))
            }
        }
        neutralButtonLabel?.also {
            builder.setNeutralButton(it) { _: DialogInterface, _: Int ->
                setFragmentResult(REQUEST_KEY,
                        bundleOf(RESULT_CLICKED_BUTTON to AlertDialog.BUTTON_NEUTRAL))
            }
        }

        return builder.create()
    }
}
