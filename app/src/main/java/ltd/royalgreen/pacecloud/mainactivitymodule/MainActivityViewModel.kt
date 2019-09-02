package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(app: Application) : ViewModel() {

    val application = app

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val userBalance: MutableLiveData<BalanceModel> by lazy {
        MutableLiveData<BalanceModel>()
    }

    fun getUserBalance(user: LoggedUser?) {
        if (isNetworkAvailable(application)) {
            apiCallStatus.postValue(ApiCallStatus.LOADING)
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user?.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.billclouduserbalance(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        userBalance.postValue(apiResponse.body)
                        val userBalanceSerialized = Gson().toJson(apiResponse.body)
                        preferences.edit().apply {
                            putString("UserBalance", userBalanceSerialized)
                            apply()
                        }
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

}