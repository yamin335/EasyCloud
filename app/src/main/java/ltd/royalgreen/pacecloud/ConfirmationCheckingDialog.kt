package ltd.royalgreen.pacecloud

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.confirmation_checking_dialog.*
import java.util.*

class ConfirmationCheckingDialog internal constructor(parentActivity: Activity, private val callBack: ConfirmationCallback, private val dialogTitle: String) : Dialog(parentActivity), View.OnClickListener {

    var firstValue: Int = 0
    var secondValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.confirmation_checking_dialog)

        title.text = dialogTitle
        refreshCaptcha()

        val answerWatcher = object : TextWatcher {
            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    value.toString().equals("", ignoreCase = true) -> {
                        yes.isEnabled = false
                        answerInputLayout.isErrorEnabled = true
                        answerInputLayout.error = "Empty Answer!"
                    }

                    value.toString().toInt() == firstValue + secondValue -> {
                        yes.isEnabled = true
                        answerInputLayout.isErrorEnabled = false
                    }

                    else -> {
                        yes.isEnabled = false
                        answerInputLayout.isErrorEnabled = true
                        answerInputLayout.error = "Invalid Answer!"
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        answer.addTextChangedListener(answerWatcher)

        recapcha.setOnClickListener(this)
        yes.setOnClickListener(this)
        no.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.yes -> {
                callBack.onYesPressed()
                dismiss()
            }
            R.id.no -> dismiss()
            R.id.recapcha -> refreshCaptcha()
        }
    }

    interface ConfirmationCallback{
        fun onYesPressed()
    }

    private fun refreshCaptcha() {
        firstValue = Random().nextInt(8)+1
        secondValue = Random().nextInt(8)+1
        question.text = "What is $firstValue + $secondValue?"
    }
}