package ltd.royalgreen.pacecloud.servicemodule

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.service_vm_note_dialog.*
import ltd.royalgreen.pacecloud.R

class VMNoteDialog internal constructor(parentActivity: Activity,
                                        private val callBack: NoteCallback,
                                        private val note: String?) : Dialog(parentActivity), View.OnClickListener {

    var newNote = note ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.service_vm_note_dialog)

        noteInput.setText(newNote)

        val noteWatcher = object : TextWatcher {
            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    value.toString().equals(note, ignoreCase = false) -> {
                        save.isEnabled = false
                    }

                    else -> {
                        save.isEnabled = true
                        newNote = value.toString()
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        noteInput.addTextChangedListener(noteWatcher)

        save.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> {
                callBack.onNoteSaved(newNote)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface NoteCallback{
        fun onNoteSaved(noteValue: String)
    }
}