package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
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
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(private val apiService: ApiService,
                                              private val preferences: SharedPreferences,
                                              private val application: Application) {

    fun osStatusRepo(apiCallStatus: MutableLiveData<ApiCallStatus>): LiveData<DashOsStatus> {
        val result = MutableLiveData<DashOsStatus>()
        if (isNetworkAvailable(application)) {
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject().apply {
                addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
                addProperty("values", "cloudvmstatus")
                addProperty("UserID", user.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.getDashboardChartPortal(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        result.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }

    fun osSummaryRepo(apiCallStatus: MutableLiveData<ApiCallStatus>): LiveData<DashOsSummary> {
        val result = MutableLiveData<DashOsSummary>()
        if (isNetworkAvailable(application)) {
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject().apply {
                addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
                addProperty("values", "cloudvm")
                addProperty("UserID", user.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.getDashboardChartPortalSummery(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        result.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
        return result
    }
}