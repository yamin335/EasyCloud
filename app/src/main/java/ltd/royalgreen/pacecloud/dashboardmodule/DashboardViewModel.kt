package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
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

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val osStatus: MutableLiveData<DashOsStatus> by lazy {
        MutableLiveData<DashOsStatus>()
    }

    val osSummary: MutableLiveData<DashOsSummary> by lazy {
        MutableLiveData<DashOsSummary>()
    }

//    lateinit var userLogs: LiveData<PagedList<CloudActivityLog>>

//    fun initializedPagedListBuilder(config: PagedList.Config):
//            LivePagedListBuilder<Long, CloudActivityLog> {
//        val dataSourceFactory = object : DataSource.Factory<Long, CloudActivityLog>() {
//            override fun create(): DataSource<Long, CloudActivityLog> {
//                return ActivityLogDataSource(application, apiService, preferences)
//            }
//        }
//        return LivePagedListBuilder<Long, CloudActivityLog>(dataSourceFactory, config)
//    }

    fun getOsStatus(user: LoggedUser) {
        if (isNetworkAvailable(application)) {
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
                val response = apiService.getDashboardChartPortal(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        osStatus.postValue(apiResponse.body)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

    fun getOsSummary(user: LoggedUser) {
        if (isNetworkAvailable(application)) {
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
                val response = apiService.getDashboardChartPortalSummery(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        osSummary.postValue(apiResponse.body)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }
}