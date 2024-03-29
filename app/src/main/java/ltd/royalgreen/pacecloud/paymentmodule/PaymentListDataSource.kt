package ltd.royalgreen.pacecloud.paymentmodule

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*

class PaymentListDataSource(private val api: ApiService,
                            private val preferences: SharedPreferences,
                            apiCallStatus: MutableLiveData<String>, jsonValues: JsonObject) : PageKeyedDataSource<Long, BilCloudUserLedger>() {

    val tempApiCallStatus = apiCallStatus
    val tempValues = jsonValues

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, BilCloudUserLedger>) {
        tempApiCallStatus.postValue("LOADING")
        var param = "[]"
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        if (user != null) {
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user.resdata?.loggeduser?.userID)
                addProperty("pageNumber", 0)
                addProperty("pageSize", 30)
                addProperty("values", tempValues.get("values").asString)
                addProperty("SDate", tempValues.get("SDate").asString)
                addProperty("EDate", tempValues.get("EDate").asString)
            }
            param = JsonArray().apply {
                add(jsonObject)
            }.toString()
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            tempApiCallStatus.postValue("ERROR")
        }

        CoroutineScope(Dispatchers.IO).launch(handler) {
            withTimeoutOrNull(3000L) {
                val response = api.billhistory(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        callback.onResult(apiResponse.body.resdata?.listBilCloudUserLedger as MutableList<BilCloudUserLedger>, null, 1)
                        tempApiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        tempApiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        tempApiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, BilCloudUserLedger>) {
        tempApiCallStatus.postValue("LOADING")
        var param = "[]"
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        if (user != null) {
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user.resdata?.loggeduser?.userID)
                addProperty("pageNumber", params.key)
                addProperty("pageSize", 30)
                addProperty("values", tempValues.get("values").asString)
                addProperty("SDate", tempValues.get("SDate").asString)
                addProperty("EDate", tempValues.get("EDate").asString)
            }
            param = JsonArray().apply {
                add(jsonObject)
            }.toString()
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            tempApiCallStatus.postValue("ERROR")
        }

        CoroutineScope(Dispatchers.IO).launch(handler) {
            withTimeoutOrNull(3000L) {
                val response = api.billhistory(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        callback.onResult(apiResponse.body.resdata?.listBilCloudUserLedger as MutableList<BilCloudUserLedger>, params.key + 1)
                        tempApiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        tempApiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        tempApiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, BilCloudUserLedger>) {

    }
}
