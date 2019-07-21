package ltd.royalgreen.pacecloud.loginmodule

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.BaseViewModel
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

class LoginViewModel : BaseViewModel(){

    @Inject
    lateinit var apiService: ApiService

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

    fun processSignIn() {
        apiCallStatus.value = ApiCallStatus.LOADING
        val jsonObject = JsonObject().apply {
            addProperty("userName", userName.value)
            addProperty("userPass", password.value)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        CoroutineScope(Dispatchers.IO).launch(handler) {
            withTimeoutOrNull(5000L) {
                val response = apiService.loginportalusers(param).execute()
                val apiResponse = ApiResponse.create(response)
                when (apiResponse) {
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
        }
    }
}