package ltd.royalgreen.pacecloud.servicemodule

//import android.app.Application
//import android.content.SharedPreferences
//import androidx.annotation.WorkerThread
//import androidx.lifecycle.MutableLiveData
//import com.google.gson.Gson
//import com.google.gson.JsonArray
//import com.google.gson.JsonObject
//import com.google.gson.JsonParser
//import kotlinx.coroutines.CoroutineExceptionHandler
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import ltd.royalgreen.pacecloud.R
//import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
//import ltd.royalgreen.pacecloud.network.*
//import ltd.royalgreen.pacecloud.util.isNetworkAvailable
//import ltd.royalgreen.pacecloud.util.showErrorToast
//import javax.inject.Inject
//
//abstract class DeploymentListLoader @Inject constructor(private val apiService: ApiService,
//                                               private val preferences: SharedPreferences,
//                                               private val application: Application
//) {
//
//    init {
//        loadDeploymentListRepo()
//    }
//
//    fun loadDeploymentListRepo() {
//        if (isNetworkAvailable(application)) {
//            var param = "[]"
//            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
//            if (user != null) {
//                val jsonObject = JsonObject().apply {
//                    addProperty("UserID", user.resdata?.loggeduser?.userID)
//                    addProperty("pageNumber", 0)
//                    addProperty("pageSize", 30)
//                }
//                param = JsonArray().apply {
//                    add(jsonObject)
//                }.toString()
//            }
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                exception.printStackTrace()
//                //apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                //apiCallStatus.postValue(ApiCallStatus.LOADING)
//                val response = apiService.cloudvmbyuserid(param)
//                when (val apiResponse = ApiResponse.create(response)) {
//                    is ApiSuccessResponse -> {
//                        val stringResponse = JsonParser.parseString(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("listCloudvm").asString
//                        if (!stringResponse.isNullOrBlank()) {
//                            val jsonArray = JsonParser.parseString(stringResponse).asJsonArray
//                            val mutableDeploymentList: MutableList<Deployment> = mutableListOf<Deployment>()
//                            for (jsonObject in jsonArray) {
//                                val deployment = Gson().fromJson(jsonObject, Deployment::class.java)
//                                mutableDeploymentList.add(deployment)
//                            }
//                            processResponse(mutableDeploymentList)
//                        }
//                        //apiCallStatus.postValue(ApiCallStatus.SUCCESS)
//                    }
//                    is ApiEmptyResponse -> {
//                        //apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                    }
//                    is ApiErrorResponse -> {
////                        apiCallStatus.postValue(ApiCallStatus.ERROR)
//                    }
//                }
//            }
//        } else {
//            showErrorToast(application, application.getString(R.string.net_error_msg))
//        }
//    }
//
//    @WorkerThread
//    protected abstract fun processResponse(deploymentList: MutableList<Deployment>)
//}