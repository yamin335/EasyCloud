package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class ServiceFragmentViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val deploymentResponse: MutableLiveData<Deployment> by lazy {
        MutableLiveData<Deployment>()
    }

    lateinit var deploymentList: LiveData<PagedList<Deployment>>

    fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, Deployment> {
        val dataSourceFactory = object : DataSource.Factory<Long, Deployment>() {
            override fun create(): DataSource<Long, Deployment> {
                return DeploymentListDataSource(application, apiService, preferences, deploymentResponse, apiCallStatus)
            }
        }
        return LivePagedListBuilder<Long, Deployment>(dataSourceFactory, config)
    }
}