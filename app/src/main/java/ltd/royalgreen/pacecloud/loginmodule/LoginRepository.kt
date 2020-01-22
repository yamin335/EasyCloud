package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService,
                                          private val preferences: SharedPreferences,
                                          private val application: Application
) {

    fun loginRepo(userName: String, password: String, apiCallStatus: MutableLiveData<ApiCallStatus>): LiveData<LoggedUser> {
        val result = MutableLiveData<LoggedUser>()
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("userName", userName)
                addProperty("userPass", password)
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.loginportalusers(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        result.postValue(apiResponse.body)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }

    fun signUpRepo(jsonObject: JsonObject, apiCallStatus: MutableLiveData<ApiCallStatus>): LiveData<String> {
        val result = MutableLiveData<String>()
        if (isNetworkAvailable(application)) {
            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.register(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        result.postValue(JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("message").asString)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }
}