package ltd.royalgreen.pacecloud.dashboardmodule

import android.app.Application

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(app: Application, dashboardRepo: DashboardRepository) : ViewModel() {

    private val repository = dashboardRepo

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
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

    fun getOsStatus() = repository.osStatusRepo(apiCallStatus)

    fun getOsSummary() = repository.osSummaryRepo(apiCallStatus)
}