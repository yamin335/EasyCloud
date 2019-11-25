package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable

class ActivityLogDataSource(private val application: Application, private val api: ApiService, private val preferences: SharedPreferences) : PageKeyedDataSource<Long, CloudActivityLog>() {
    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, CloudActivityLog>) {
        if (isNetworkAvailable(application)) {
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
//            apiCallStatus.value = ApiCallStatus.LOADING

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(7000L) {
                    val response = api.cloudactivitylog(param)
                    when (val apiResponse = ApiResponse.create(response)) {
                        is ApiSuccessResponse -> {
                            callback.onResult(apiResponse.body.resdata?.listCmnUserCloudActivityLog as MutableList<CloudActivityLog>, null, 1)
//                            osStatus.postValue(apiResponse.body)
//                            apiCallStatus.value = ApiCallStatus.SUCCESS
                        }
                        is ApiEmptyResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
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

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, CloudActivityLog>) {
        if (isNetworkAvailable(application)) {
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
//            apiCallStatus.value = ApiCallStatus.LOADING

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(7000L) {
                    val response = api.cloudactivitylog(param)
                    when (val apiResponse = ApiResponse.create(response)) {
                        is ApiSuccessResponse -> {
                            callback.onResult(apiResponse.body.resdata?.listCmnUserCloudActivityLog as MutableList<CloudActivityLog>, params.key + 1)
//                            osStatus.postValue(apiResponse.body)
//                            apiCallStatus.value = ApiCallStatus.SUCCESS
                        }
                        is ApiEmptyResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
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

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, CloudActivityLog>) {

    }
}
