package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable

class DeploymentListDataSource(private val application: Application, private val api: ApiService,
                               private val preferences: SharedPreferences, deployment: MutableLiveData<Deployment>, apiCallStatus: MutableLiveData<ApiCallStatus>) : PageKeyedDataSource<Long, Deployment>() {

    val tempDeployment = deployment
    val tempApiCallStatus = apiCallStatus

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Deployment>) {
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
                tempApiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = api.cloudvmbyuserid(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val stringResponse = JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
                        val jsonArray = JsonParser().parse(stringResponse).asJsonArray
                        val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
                        for ((index, jsonObject) in jsonArray.withIndex()) {
                            val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
                            mutableDeploymentList.add(deployment)
                            if (index == 0)
                                tempDeployment.postValue(deployment)
                        }
                        callback.onResult(mutableDeploymentList, null, 1)
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
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Deployment>) {
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
                tempApiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = api.cloudvmbyuserid(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val stringResponse = JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
                        val jsonArray = JsonParser().parse(stringResponse).asJsonArray
                        val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
                        for (jsonObject in jsonArray) {
                            val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
                            mutableDeploymentList.add(deployment)
                        }
                        callback.onResult(mutableDeploymentList, params.key + 1)
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
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Deployment>) {

    }
}
