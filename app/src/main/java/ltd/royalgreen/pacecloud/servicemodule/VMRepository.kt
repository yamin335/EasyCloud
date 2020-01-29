package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VMRepository @Inject constructor(private val apiService: ApiService,
                                       private val preferences: SharedPreferences,
                                       private val application: Application
) {

    fun deploymentRenameRepo(deploymentId: Number?, renamedValue: String?): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        if (isNetworkAvailable(application)) {
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user?.resdata?.loggeduser?.userID)
                addProperty("id", deploymentId)
                addProperty("Name", renamedValue)
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.updatedeploymentname(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        result.postValue(JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean)
                    }
                    is ApiEmptyResponse -> {
                        Log.d("EMPTY","EMPTY_VALUE")
                    }
                    is ApiErrorResponse -> {
                        Log.d("ERROR","ERROR_RESPONSE")
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }

    fun syncAndRefreshVMRepo(): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        if (isNetworkAvailable(application)) {
            //apiCallStatus.postValue(ApiCallStatus.LOADING)
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject()
            user?.let {
                jsonObject.apply {
                    addProperty("pageNumber", "1")
                    addProperty("pageSize", "20")
                    addProperty("id", it.resdata?.loggeduser?.userID?.toInt() ?: 0)
                }
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                //apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.clouduservmsyncwithlocaldb(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        //apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        result.postValue(JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean)
                    }
                    is ApiEmptyResponse -> {
                        //apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        //apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }

    fun runningVMStatusRepo(): MutableLiveData<Deployment> {
        val result = MutableLiveData<Deployment>()
        if (isNetworkAvailable(application)) {
            var param = "[]"
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            if (user != null) {
                val jsonObject = JsonObject().apply {
                    addProperty("UserID", user.resdata?.loggeduser?.userID)
                    addProperty("pageNumber", 0)
                    addProperty("pageSize", 1)
                }
                param = JsonArray().apply {
                    add(jsonObject)
                }.toString()
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                //apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                //apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.cloudvmbyuserid(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val stringResponse = JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
                        if (!stringResponse.isNullOrBlank()) {
                            val jsonArray = JsonParser.parseString(stringResponse).asJsonArray
                            val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
                            for ((_, jsonObject) in jsonArray.withIndex()) {
                                val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
                                mutableDeploymentList.add(deployment)
                            }
                            result.postValue(mutableDeploymentList[0])
                        }
                        //apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    }
                    is ApiEmptyResponse -> {
                        //apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        //apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }

    suspend fun loadDeploymentPagedListRepo(param: String) = withContext(Dispatchers.IO) { apiService.cloudvmbyuserid(param) }
}