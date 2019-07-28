package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject

class DashboardViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    private val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val osStatus: MutableLiveData<DashOsStatus> by lazy {
        MutableLiveData<DashOsStatus>()
    }

    val osSummary: MutableLiveData<DashOsSummary> by lazy {
        MutableLiveData<DashOsSummary>()
    }

    val userLogResponse: MutableLiveData<UserActivityLog> by lazy {
        MutableLiveData<UserActivityLog>()
    }

    lateinit var userLogs: LiveData<PagedList<CloudActivityLog>>

    fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, CloudActivityLog> {
        val dataSourceFactory = object : DataSource.Factory<Long, CloudActivityLog>() {
            override fun create(): DataSource<Long, CloudActivityLog> {
                return ActivityLogDataSource(application, apiService, preferences)
            }
        }
        return LivePagedListBuilder<Long, CloudActivityLog>(dataSourceFactory, config)
    }

    fun getOsStatus(user: LoggedUser) {
        if (isNetworkAvailable(application)) {
            apiCallStatus.value = ApiCallStatus.LOADING
            val jsonObject = JsonObject().apply {
                addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
                addProperty("values", "cloudvmstatus")
                addProperty("UserID", user.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(5000L) {
                    val response = apiService.GetDashboardChartPortal(param).execute()
                    val apiResponse = ApiResponse.create(response)
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            osStatus.postValue(apiResponse.body)
                            apiCallStatus.value = ApiCallStatus.SUCCESS
                        }
                        is ApiEmptyResponse -> {
                            apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
                            apiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }

    fun getOsSummary(user: LoggedUser) {
        if (isNetworkAvailable(application)) {
            apiCallStatus.value = ApiCallStatus.LOADING
            val jsonObject = JsonObject().apply {
                addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
                addProperty("values", "cloudvm")
                addProperty("UserID", user.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(5000L) {
                    val response = apiService.GetDashboardChartPortalSummery(param).execute()
                    val apiResponse = ApiResponse.create(response)
                    when (apiResponse) {
                        is ApiSuccessResponse -> {
                            osSummary.postValue(apiResponse.body)
                            apiCallStatus.value = ApiCallStatus.SUCCESS
                        }
                        is ApiEmptyResponse -> {
                            apiCallStatus.postValue(ApiCallStatus.EMPTY)
                        }
                        is ApiErrorResponse -> {
                            apiCallStatus.postValue(ApiCallStatus.ERROR)
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }
}