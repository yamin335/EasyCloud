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
import ltd.royalgreen.pacecloud.util.showErrorToast


class LoginFragmentViewModel @Inject constructor(app: Application, loginRepository: LoginRepository) : ViewModel() {

    val repository = loginRepository

    val userName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
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

    fun doSignIn() = repository.loginRepo(userName.value!!, password.value!!, apiCallStatus)

    fun doSignUp(jsonObject: JsonObject) = repository.signUpRepo(jsonObject, apiCallStatus)

//    val signUpMsg: MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }

//    fun processSignIn() {
//        if (isNetworkAvailable(application)) {
//            val jsonObject = JsonObject().apply {
//                addProperty("userName", userName.value)
//                addProperty("userPass", password.value)
//            }
//
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.loginportalusers(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiResult.postValue(apiResponse.body)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
//
//    fun processSignUp(jsonObject: JsonObject) {
//        if (isNetworkAvailable(application)) {
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.register(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                        signUpMsg.postValue(JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("message").asString)
//                    }
//                    is ApiEmptyResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
//                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
}