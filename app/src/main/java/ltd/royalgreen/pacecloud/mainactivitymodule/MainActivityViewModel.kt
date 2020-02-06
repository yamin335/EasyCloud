package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.PaymentRepository
import ltd.royalgreen.pacecloud.util.ConnectivityLiveData
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val application: Application, private val repository: PaymentRepository) : BaseViewModel() {

    @Inject
    lateinit var preferences: SharedPreferences

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }

    fun getUserBalance(): LiveData<BalanceModel> {
        val userBalance = MutableLiveData<BalanceModel>()
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.usrBalanceRepo())) {
                    is ApiSuccessResponse -> {
                        userBalance.postValue(apiResponse.body)
                        val userBalanceSerialized = Gson().toJson(apiResponse.body)
                        preferences.edit().apply {
                            putString("UserBalance", userBalanceSerialized)
                            apply()
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
        return userBalance
    }

}