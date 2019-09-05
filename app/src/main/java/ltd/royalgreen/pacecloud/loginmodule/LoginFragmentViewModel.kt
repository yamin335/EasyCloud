package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject

class LoginFragmentViewModel @Inject constructor(app: Application) : ViewModel(){

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val userName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val apiResult: MutableLiveData<LoggedUser> by lazy {
        MutableLiveData<LoggedUser>()
    }

    val errorMessage: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val firstName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isValidEmail: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val mobile: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isValidPhone: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val signUpPass: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val signUpConfPass: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val company: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val signUpMsg: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun processSignIn() {
        if (isNetworkAvailable(application)) {
            apiCallStatus.value = ApiCallStatus.LOADING
            val jsonObject = JsonObject().apply {
                addProperty("userName", userName.value)
                addProperty("userPass", password.value)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.loginportalusers(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiResult.postValue(apiResponse.body)
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

    fun processSignUp(jsonObject: JsonObject) {
        if (isNetworkAvailable(application)) {
            apiCallStatus.postValue(ApiCallStatus.LOADING)

            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.register(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        signUpMsg.postValue(JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("message").asString)
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