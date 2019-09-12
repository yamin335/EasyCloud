package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.confirmation_checking_dialog.*
import kotlinx.android.synthetic.main.confirmation_checking_dialog.view.*
import ltd.royalgreen.pacecloud.R
import java.util.*

class ConfirmationCheckingDialog internal constructor(private val callBack: ConfirmationCallback, private val dialogTitle: String) : DialogFragment(), View.OnClickListener {

    var firstValue: Int = 0
    var secondValue: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.confirmation_checking_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.title.text = dialogTitle
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

                    value.toString() == "${firstValue + secondValue}" -> {
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

        view.answer.addTextChangedListener(answerWatcher)

        view.recapcha.setOnClickListener(this)
        view.yes.setOnClickListener(this)
        view.no.setOnClickListener(this)
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
        yes.isEnabled = false
        firstValue = Random().nextInt(8)+1
        secondValue = Random().nextInt(8)+1
        question.text = "What is $firstValue + $secondValue?"
        if (!answer.text.toString().isBlank()) {
            if (answer.text.toString() == "${firstValue + secondValue}") {
                yes.isEnabled = true
                answerInputLayout.isErrorEnabled = false
            } else {
                yes.isEnabled = false
                answerInputLayout.isErrorEnabled = true
                answerInputLayout.error = "Invalid Answer!"
            }
        }
    }
}