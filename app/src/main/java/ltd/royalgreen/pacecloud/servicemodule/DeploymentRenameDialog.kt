package ltd.royalgreen.pacecloud.servicemodule

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.service_deployment_rename_dialog.*
import ltd.royalgreen.pacecloud.R

class DeploymentRenameDialog internal constructor(parentActivity: Activity,
                                                  private val callBack: RenameCallback,
                                                  private val dialogTitle: String?,
                                                  private val name: String?) : Dialog(parentActivity), View.OnClickListener {

    var rename = name ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.service_deployment_rename_dialog)

        title.text = dialogTitle ?: "Change Deployment Name"
        renameInput.setText(rename)

        val renameWatcher = object : TextWatcher {
            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    value.toString().equals("", ignoreCase = true) -> {
                        save.isEnabled = false
                        renameInputLayout.isErrorEnabled = true
                        renameInputLayout.error = "Empty Name!"
                    }

                    value.toString().equals(name, ignoreCase = false) -> {
                        save.isEnabled = false
                        renameInputLayout.isErrorEnabled = false
                    }

                    "^(?=[A-Za-z]).*".toRegex().matches(value) -> {
                        save.isEnabled = true
                        renameInputLayout.isErrorEnabled = false
                        rename = value.toString()
                    }

                    else -> {
                        save.isEnabled = false
                        renameInputLayout.isErrorEnabled = true
                        renameInputLayout.error = "Invalid Name!"
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        renameInput.addTextChangedListener(renameWatcher)

        save.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> {
                callBack.onSavePressed(rename)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface RenameCallback{
        fun onSavePressed(renamedValue: String)
    }
}