package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import javax.inject.Inject
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import ltd.royalgreen.pacecloud.network.ApiEmptyResponse
import ltd.royalgreen.pacecloud.network.ApiErrorResponse
import ltd.royalgreen.pacecloud.network.ApiResponse
import ltd.royalgreen.pacecloud.network.ApiSuccessResponse

class LoginFragmentViewModel @Inject constructor(private val application: Application, private val repository: LoginRepository) : BaseViewModel() {

    val userName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
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

    fun doSignIn(): LiveData<LoggedUser> {
        val loggedUser = MutableLiveData<LoggedUser>()
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.loginRepo(userName.value!!, password.value!!))) {
                    is ApiSuccessResponse -> {
                        loggedUser.postValue(apiResponse.body)
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
        return loggedUser
    }

    fun doSignUp(jsonObject: JsonObject): LiveData<String> {
        val response = MutableLiveData<String>()
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.signUpRepo(jsonObject))) {
                    is ApiSuccessResponse -> {
                        response.postValue(JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("message").asString)
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
        return response
    }
}