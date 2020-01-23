package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class ServiceFragmentViewModel @Inject constructor(app: Application, vmRepository: VMRepository) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val repository = vmRepository

    val dispachers: CoroutineDispatcher = Dispatchers.IO

    val refreshDatabaseAndUI: MutableLiveData<Boolean>
        get() = syncDatabaseAndRefresh()

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val runningVmStatus: MutableLiveData<Deployment>
        get() = loadRunnungVMStatus()

    lateinit var deploymentList: LiveData<PagedList<Deployment>>

    fun renameDeployment(deploymentId: Number?, renameValue: String) = repository.deploymentRenameRepo(deploymentId, renameValue)

    fun syncDatabaseAndRefresh() = repository.syncAndRefreshVMRepo(apiCallStatus)

    fun loadRunnungVMStatus() = repository.runningVMStatusRepo(apiCallStatus)

    fun initializedPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Long, Deployment> {
        val dataSourceFactory = object : DataSource.Factory<Long, Deployment>() {
            override fun create(): DataSource<Long, Deployment> {
                return DeploymentListDataSource(dispachers, apiService, preferences, apiCallStatus)
            }
        }
        return LivePagedListBuilder<Long, Deployment>(dataSourceFactory, config)
    }
}