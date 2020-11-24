package jp.co.c_lis.ccl.morelocale.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import jp.co.c_lis.ccl.morelocale.BuildConfig
import jp.co.c_lis.ccl.morelocale.R

class AboutDialog : DialogFragment() {

    companion object {
        val TAG = AboutDialog::class.simpleName

        fun getInstance(): AboutDialog {
            return AboutDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.dialog_about, null).also { view ->
            val versionText = view.findViewById<TextView>(R.id.version)
            versionText.text = "Version ${BuildConfig.VERSION_NAME}"
        }

        return Dialog(requireContext()).also { d ->
            d.setContentView(view)
        }
    }
}
