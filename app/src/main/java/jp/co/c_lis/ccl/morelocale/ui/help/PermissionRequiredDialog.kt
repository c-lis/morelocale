package jp.co.c_lis.ccl.morelocale.ui.help

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import jp.co.c_lis.ccl.morelocale.R

class PermissionRequiredDialog : AppCompatDialogFragment() {

    companion object {
        val TAG = PermissionRequiredDialog::class.simpleName

        fun getInstance(): PermissionRequiredDialog {
            return PermissionRequiredDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext(), R.style.theme_permission_dialog)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.permission_required_message)
                .setPositiveButton(R.string.use_pm_command) { _, _ ->
                    HowToUsePmCommandDialog.getInstance()
                            .show(parentFragmentManager, HowToUsePmCommandDialog.TAG)
                }
                .setNeutralButton(R.string.execute_as_su) { _, _ ->

                }
                .create()
    }
}
