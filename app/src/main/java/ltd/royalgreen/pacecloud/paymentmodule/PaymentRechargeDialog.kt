package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.payment_recharge_dialog.*
import ltd.royalgreen.pacecloud.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class PaymentRechargeDialog internal constructor(private val callBack: RechargeCallback) : DialogFragment(), View.OnClickListener {

    var selectedDate = ""
    var rechargeAmount = ""
    var rechargeNote = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_recharge_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amountWatcher = object : TextWatcher {
            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    value.toString().equals("", ignoreCase = true) -> {
                        save.isEnabled = false
                        amountInputLayout.isErrorEnabled = true
                        amountInputLayout.error = "Empty Amount!"
                    }

                    "^(?=\\d)(?=.*[1-9])(\\d*)\\.?\\d+".toRegex().matches(value) -> {
                        save.isEnabled = true
                        amountInputLayout.isErrorEnabled = false
                        rechargeAmount = value.toString()
                    }

                    else -> {
                        save.isEnabled = false
                        amountInputLayout.isErrorEnabled = true
                        amountInputLayout.error = "Invalid Amount!"
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        amount.addTextChangedListener(amountWatcher)

        //Current Date
        val today = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = df.format(today)
        save.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> {
                rechargeNote = note.text.toString()
                callBack.onSavePressed(selectedDate, rechargeAmount, rechargeNote)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface RechargeCallback{
        fun onSavePressed(date: String, amount: String, note: String)
    }
}