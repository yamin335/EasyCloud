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

    val vmResponse: MutableLiveData<VMListResponse> by lazy {
        MutableLiveData<VMListResponse>()
    }

    lateinit var vmList: LiveData<PagedList<VM>>

    fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, VM> {
        val dataSourceFactory = object : DataSource.Factory<Long, VM>() {
            override fun create(): DataSource<Long, VM> {
                return VMListDataSource(application, apiService, preferences, vmResponse, apiCallStatus)
            }
        }
        return LivePagedListBuilder<Long, VM>(dataSourceFactory, config)
    }
}