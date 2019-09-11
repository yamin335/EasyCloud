package ltd.royalgreen.pacecloud.servicemodule

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.service_vm_note_dialog.*
import ltd.royalgreen.pacecloud.R

class VMNoteDialog internal constructor(private val callBack: NoteCallback,
                                        private val note: String?) : DialogFragment(), View.OnClickListener {

    var newNote = note ?: ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.service_vm_note_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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