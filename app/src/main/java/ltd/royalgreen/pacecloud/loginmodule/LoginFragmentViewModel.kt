package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
}