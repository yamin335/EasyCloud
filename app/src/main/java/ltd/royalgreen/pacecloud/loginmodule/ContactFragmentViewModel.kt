package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import javax.inject.Inject

class ContactFragmentViewModel @Inject constructor(private val application: Application) : BaseViewModel() {

    val currentNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}