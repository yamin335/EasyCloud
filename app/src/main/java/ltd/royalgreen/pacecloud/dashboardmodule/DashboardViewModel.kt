package ltd.royalgreen.pacecloud.dashboardmodule

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ltd.royalgreen.pacecloud.BaseViewModel
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

const val SHARED_PREFS_KEY = "LoginStatus"

class DashboardViewModel : BaseViewModel() {
    @Inject
    lateinit var apiService: ApiService

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val userBalance: MutableLiveData<BalanceModel> by lazy {
        MutableLiveData<BalanceModel>()
    }

    val osStatus: MutableLiveData<DashOsStatus> by lazy {
        MutableLiveData<DashOsStatus>()
    }

    val osSummary: MutableLiveData<DashOsSummary> by lazy {
        MutableLiveData<DashOsSummary>()
    }

    init {
        //Get LoggedUser data from shared preference
//        val sharedPref = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
//        val loggedUser = sharedPreferences.getString(USER_PROFILE, null)
//        return gson.fromJson(serializedUser, UserProfile::class.java)

    }

    private fun getUserBalance() {

    }

    private fun getOsStatus() {

    }

    private fun getOsSummary() {

    }
}