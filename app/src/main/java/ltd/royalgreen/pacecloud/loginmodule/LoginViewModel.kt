package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.ConnectivityLiveData
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject

class LoginViewModel @Inject constructor(app: Application) : ViewModel(){

    val application = app

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }
}