package ltd.royalgreen.pacecloud.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import retrofit2.Response

///A generic class that can provide a resource by the network.
abstract class NetworkBoundResource<RequestType, ResultType> constructor(private val context: Application, private val apiService: ApiService, apiCallStatus: MutableLiveData<String>) {

    private val result = MutableLiveData<ResultType>()
    private val loadingStatus = apiCallStatus

    init {
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: ResultType) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {

//        if (isNetworkAvailable(context)) {
//
//            val param = createCall()
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                val response = when(apiName()) {
//                    is "getDashboardChartPortal" -> {
//                        apiService.getDashboardChartPortal(param as String)
//                    }
//                    else -> {
//                        Log.d("", "")
//                    }
//                }
//                if (response.isSuccessful) {
//                    result.postValue(response.body())
//                }
//                print(response.toString())
////                when (val apiResponse = ApiResponse.create(response)) {
////                    is ApiSuccessResponse -> {
////                        osStatus.postValue(apiResponse.body)
////                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
////                    }
////                    is ApiEmptyResponse -> {
////                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
////                    }
////                    is ApiErrorResponse -> {
////                        apiCallStatus.postValue(ApiCallStatus.ERROR)
////                    }
////                }
//            }
//        } else {
//            showErrorToast(context, context.getString(R.string.net_error_msg))
//        }
//
//
//        val handler = CoroutineExceptionHandler { _, exception ->
//            exception.printStackTrace()
//        }
//        CoroutineScope(Dispatchers.IO).launch(handler) {
//            val response = createCall()
//            setValue(response.value)
////            when (val apiResponse = ApiResponse.create(response.value)) {
////                is ApiSuccessResponse -> {
////                    setValue(apiResponse.body)
////                }
////                is ApiEmptyResponse -> {
////
////                }
////                is ApiErrorResponse -> {
////                    onFetchFailed(apiResponse.errorMessage)
////                }
////            }
//        }
    }

    protected abstract fun onFetchFailed(errorMessage: String)

    fun asLiveData() = result

    protected abstract fun createCall(): RequestType

    protected abstract fun apiName(): String

}