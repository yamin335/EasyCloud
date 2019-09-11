package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.ConnectivityLiveData
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

    val internetStatus: ConnectivityLiveData by lazy {
        ConnectivityLiveData(application)
    }

    fun getUserBalance(user: LoggedUser?) {
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user?.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

}