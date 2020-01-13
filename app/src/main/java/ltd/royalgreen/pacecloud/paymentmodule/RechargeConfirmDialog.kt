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

class RechargeConfirmDialog internal constructor(private val callBack: RechargeConfirmCallback, private val rechargeAmount: String, private val note: String) : DialogFragment(), View.OnClickListener {

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
        bKashRecharge.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.processToRecharge -> {
                callBack.onFosterClicked(amount = rechargeAmount, note = note)
                dismiss()
            }

            R.id.bKashRecharge -> {
                callBack.onBKashClicked(amount = rechargeAmount)
                dismiss()
            }

            R.id.cancel -> dismiss()
        }
    }

    interface RechargeConfirmCallback{
        fun onFosterClicked(amount: String, note: String)
        fun onBKashClicked(amount: String)
    }
}