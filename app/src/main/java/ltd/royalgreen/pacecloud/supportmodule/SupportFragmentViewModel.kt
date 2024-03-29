package ltd.royalgreen.pacecloud.supportmodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class SupportFragmentViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val apiCallStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}