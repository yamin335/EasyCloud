package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ltd.royalgreen.pacecloud.R

class CustomAlertDialog internal constructor(private val callBack: YesCallback, private val title: String, private val subTitle: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setIcon(R.mipmap.app_logo_new)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                callBack.onYes()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        return exitDialog.create()
    }

    interface YesCallback{
        fun onYes()
    }
}