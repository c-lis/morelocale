package jp.co.c_lis.ccl.morelocale.ui.help

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.stericson.RootShell.execution.Command
import com.stericson.RootTools.RootTools
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.R
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeoutException

class PermissionRequiredDialog : AppCompatDialogFragment() {

    companion object {
        val TAG = PermissionRequiredDialog::class.simpleName

        private const val TIMEOUT = 15 * 1000
        private const val COMMAND_PM_GRANT = "pm grant jp.co.c_lis.ccl.morelocale android.permission.CHANGE_CONFIGURATION"

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
                    if (getPermissionAsSuperUser()) {
                        Toast.makeText(requireContext(),
                                R.string.permission_granted, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(),
                                R.string.could_not_get_su_privilege, Toast.LENGTH_LONG).show()
                    }
                }
                .create()
    }

    private fun getPermissionAsSuperUser(): Boolean {
        try {
            RootTools.debugMode = BuildConfig.DEBUG
            RootTools.getShell(true, TIMEOUT)
                    .add(Command(0, COMMAND_PM_GRANT))
            return true
        } catch (e: InterruptedException) {
            Timber.e(e)
        } catch (e: IOException) {
            Timber.e(e)
        } catch (e: TimeoutException) {
            Timber.e(e)
        }
        return false
    }
}
