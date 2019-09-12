package ltd.royalgreen.pacecloud.network

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.net_status_dialog.view.*
import ltd.royalgreen.pacecloud.R

class NetworkStatusDialog internal constructor(private val callBack: NetworkChangeCallback) : DialogFragment(), View.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.net_status_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.exitApp.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.exitApp -> {
                callBack.onExit()
                dismiss()
            }
        }
    }

    interface NetworkChangeCallback{
        fun onExit()
    }
}