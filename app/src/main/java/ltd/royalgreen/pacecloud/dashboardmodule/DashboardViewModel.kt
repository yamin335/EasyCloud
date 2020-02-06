package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val application: Application,
                                             private val repository: DashboardRepository
) : BaseViewModel() {

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

    fun getOsStatus(): LiveData<DashOsStatus> {
        val osStatus = MutableLiveData<DashOsStatus>()
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.osStatusRepo())) {
                    is ApiSuccessResponse -> {
                        osStatus.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
        return osStatus
    }

    fun getOsSummary(): MutableLiveData<DashOsSummary> {
        val osSummary = MutableLiveData<DashOsSummary>()
        if (checkNetworkStatus(application)) {
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.osSummaryRepo())) {
                    is ApiSuccessResponse -> {
                        osSummary.postValue(apiResponse.body)
                    }
                    is ApiEmptyResponse -> {
                    }
                    is ApiErrorResponse -> {
                    }
                }
            }
        }
        return osSummary
    }
}