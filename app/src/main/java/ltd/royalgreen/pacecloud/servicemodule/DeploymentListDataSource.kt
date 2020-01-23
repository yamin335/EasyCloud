package ltd.royalgreen.pacecloud.servicemodule

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*

class DeploymentListDataSource(dispacher: CoroutineDispatcher, private val api: ApiService, private val preferences: SharedPreferences
                               , apiCallStatus: MutableLiveData<ApiCallStatus>) : PageKeyedDataSource<Long, Deployment>() {

    val dispachers = dispacher
    val tempApiCallStatus = apiCallStatus

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Deployment>) {
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
            exception.printStackTrace()
            tempApiCallStatus.postValue(ApiCallStatus.ERROR)
        }

        CoroutineScope(dispachers).launch(handler) {
            tempApiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = api.cloudvmbyuserid(param)
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    val stringResponse = JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
                    if (!stringResponse.isNullOrBlank()) {
                        val jsonArray = JsonParser.parseString(stringResponse).asJsonArray
                        val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
                        for ((index, jsonObject) in jsonArray.withIndex()) {
                            val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
                            mutableDeploymentList.add(deployment)
                        }
                        callback.onResult(mutableDeploymentList, null, 1)
                    }
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

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Deployment>) {
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
            exception.printStackTrace()
            tempApiCallStatus.postValue(ApiCallStatus.ERROR)
        }

        CoroutineScope(dispachers).launch(handler) {
            tempApiCallStatus.postValue(ApiCallStatus.LOADING)
            val response = api.cloudvmbyuserid(param)
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    val stringResponse = JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
                    if (!stringResponse.isNullOrBlank()) {
                        val jsonArray = JsonParser.parseString(stringResponse).asJsonArray
                        val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
                        for (jsonObject in jsonArray) {
                            val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
                            mutableDeploymentList.add(deployment)
                        }
                        callback.onResult(mutableDeploymentList, params.key + 1)
                    }
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

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Deployment>) {

    }
}
