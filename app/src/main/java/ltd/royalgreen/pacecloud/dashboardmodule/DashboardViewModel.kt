package ltd.royalgreen.pacecloud.dashboardmodule

import androidx.lifecycle.MutableLiveData
import ltd.royalgreen.pacecloud.BaseViewModel
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class DashboardViewModel : BaseViewModel() {
    @Inject
    lateinit var apiService: ApiService

//    @Inject
//    lateinit var loggedUser: LoggedUser

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