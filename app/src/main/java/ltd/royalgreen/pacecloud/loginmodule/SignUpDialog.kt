package ltd.royalgreen.pacecloud.loginmodule

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.sign_up_dialog.view.*
import ltd.royalgreen.pacecloud.R

class SignUpDialog internal constructor(private val callBack: SignUpCallback) : DialogFragment(), View.OnClickListener {

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sign_up_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        val renameWatcher = object : TextWatcher {
//            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
//                when {
//                    value.toString().equals("", ignoreCase = true) -> {
//                        save.isEnabled = false
//                        renameInputLayout.isErrorEnabled = true
//                        renameInputLayout.error = "Empty Name!"
//                    }
//
//                    value.toString().equals(name, ignoreCase = false) -> {
//                        save.isEnabled = false
//                        renameInputLayout.isErrorEnabled = false
//                    }
//
//                    "^(?=[A-Za-z]).*".toRegex().matches(value) -> {
//                        save.isEnabled = true
//                        renameInputLayout.isErrorEnabled = false
//                        rename = value.toString()
//                    }
//
//                    else -> {
//                        save.isEnabled = false
//                        renameInputLayout.isErrorEnabled = true
//                        renameInputLayout.error = "Invalid Name!"
//                    }
//
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {
//
//            }
//        }
//
//        renameInput.addTextChangedListener(renameWatcher)
//
        view.signUp.setOnClickListener(this)
        view.cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signUp -> {
                callBack.onSignUp(JsonObject())
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface SignUpCallback{
        fun onSignUp(newUser: JsonObject)
    }
}