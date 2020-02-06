package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast

open class BaseViewModel : ViewModel() {

    val apiCallStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun checkNetworkStatus(application: Application) = if (isNetworkAvailable(application)) {
        true
    } else {
        showErrorToast(application, application.getString(R.string.net_error_msg))
        false
    }

    fun onAppExit(preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("LoggedUser", "")
            apply()
        }
    }
}