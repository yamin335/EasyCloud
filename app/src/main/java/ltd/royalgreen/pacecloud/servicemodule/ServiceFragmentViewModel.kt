package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class ServiceFragmentViewModel @Inject constructor(vmRepository: VMRepository) : ViewModel() {

    val repository = vmRepository

    val refreshDatabaseAndUI: MutableLiveData<Boolean>
        get() = syncDatabaseAndRefresh()

    val apiCallStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val runningVmStatus: MutableLiveData<Deployment>
        get() = loadRunnungVMStatus()

    lateinit var deploymentList: LiveData<PagedList<Deployment>>

    fun renameDeployment(deploymentId: Number?, renameValue: String) = repository.deploymentRenameRepo(deploymentId, renameValue)

    fun syncDatabaseAndRefresh() = repository.syncAndRefreshVMRepo()

    fun loadRunnungVMStatus() = repository.runningVMStatusRepo()

    suspend fun loadVMListData(param:String) = repository.loadDeploymentPagedListRepo(param)
}