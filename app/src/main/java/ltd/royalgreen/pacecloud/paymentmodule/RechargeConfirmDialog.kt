package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.payment_recharge_confirm_dialog.*
import ltd.royalgreen.pacecloud.R

class RechargeConfirmDialog internal constructor(private val callBack: RechargeConfirmCallback, private val rechargeAmount: String?, private val note: String, private val rechargeUrl: String?) : DialogFragment(), View.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_recharge_confirm_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rechargeQuestion.text = "Recharge Amount: $rechargeAmount"
        rechargeNote.text = "Note: $note"
        processToRecharge.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.processToRecharge -> {
                callBack.onClicked(rechargeUrl)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface RechargeConfirmCallback{
        fun onClicked(url: String?)
    }
}