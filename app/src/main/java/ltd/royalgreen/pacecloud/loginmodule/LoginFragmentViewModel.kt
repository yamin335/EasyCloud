package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject
import android.content.Context.LAYOUT_INFLATER_SERVICE
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import ltd.royalgreen.pacecloud.util.ConnectivityLiveData


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
            val jsonObject = JsonObject().apply {
                addProperty("userName", userName.value)
                addProperty("userPass", password.value)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

    fun processSignUp(jsonObject: JsonObject) {
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }
}