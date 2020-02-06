package ltd.royalgreen.pacecloud.mainactivitymodule

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {
    /**
     * Hides keyboard when the [EditText] is focused.
     *
     * Note that there can only be one [TextView.OnEditorActionListener] on each [EditText] and
     * this [BindingAdapter] sets it.
     */
    @JvmStatic
    @BindingAdapter("hideKeyboardOnInputDone")
    fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
        if (!enabled) return
        val listener = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.clearFocus()
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }
        view.setOnEditorActionListener(listener)
    }

    // Shows and hides progressbar
    @JvmStatic
    @BindingAdapter("isVisible")
    fun isVisible(view: View, apiCallStatus: String?) {
        view.visibility = if (apiCallStatus?.equals("LOADING") == true) View.VISIBLE else View.GONE
    }

    // Shows and hides progressbar
//    @JvmStatic
//    @BindingAdapter("isVisibleOrGone")
//    fun isVisibleOrGone(view: View, apiCallStatus: String?) {
//        view.visibility = if (apiCallStatus?.equals("LOADING") == true) View.VISIBLE else View.GONE
//    }

    // Shows and hides progressbar
//    @JvmStatic
//    @BindingAdapter("showLoader")
//    fun showLoader(view: View, apiCallStatus: ApiCallStatus?) {
//        view.visibility = if (apiCallStatus?.equals(ApiCallStatus.LOADING) == true) View.VISIBLE else View.GONE
//    }

    // Shows and hides errorText showLoader
    @JvmStatic
    @BindingAdapter("showIfInvalid")
    fun showIfInvalid(view: View, errorMessage: Boolean?) {
        view.visibility = if (errorMessage == true) View.VISIBLE else View.GONE
    }
}