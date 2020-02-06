package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import ltd.royalgreen.pacecloud.util.ConnectivityLiveData
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val application: Application) : BaseViewModel(){

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }
}