package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable

class VMListDataSource(private val application: Application, private val api: ApiService,
                       private val preferences: SharedPreferences, vmResponse: MutableLiveData<VMListResponse>, apiCallStatus: MutableLiveData<ApiCallStatus>) : PageKeyedDataSource<Long, VM>() {

    val tempVMResponse = vmResponse
    val tempApiCallStatus = apiCallStatus

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, VM>) {
        if (isNetworkAvailable(application)) {
            tempApiCallStatus.postValue(ApiCallStatus.LOADING)
            var param = "[]"
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            if (user != null) {
                val jsonObject = JsonObject().apply {
                    addProperty("UserID", user.resdata?.loggeduser?.userID)
                    addProperty("pageNumber", 0)
                    addProperty("pageSize", 30)
                }
                param = JsonArray().apply {
                    add(jsonObject)
                }.toString()
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(5000L) {
                    val response = api.cloudvmbyuserid(param).execute()
                    when (val apiResponse = ApiResponse.create(response)) {
                        is ApiSuccessResponse -> {
                            callback.onResult(apiResponse.body.resdata?.listCloudvm as MutableList<VM>, null, 1)
                            tempVMResponse.postValue(apiResponse.body)
                            tempApiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        }
                        is ApiEmptyResponse -> {
                            tempApiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
                            tempApiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, VM>) {
        if (isNetworkAvailable(application)) {
            tempApiCallStatus.postValue(ApiCallStatus.LOADING)
            var param = "[]"
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            if (user != null) {
                val jsonObject = JsonObject().apply {
                    addProperty("UserID", user.resdata?.loggeduser?.userID)
                    addProperty("pageNumber", params.key + 1)
                    addProperty("pageSize", 30)
                }
                param = JsonArray().apply {
                    add(jsonObject)
                }.toString()
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(5000L) {
                    val response = api.cloudvmbyuserid(param).execute()
                    when (val apiResponse = ApiResponse.create(response)) {
                        is ApiSuccessResponse -> {
                            callback.onResult(apiResponse.body.resdata?.listCloudvm as MutableList<VM>, params.key + 1)
                            tempApiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        }
                        is ApiEmptyResponse -> {
                            tempApiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
                            tempApiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, VM>) {

    }
}