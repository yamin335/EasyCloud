package ltd.royalgreen.pacecloud.network

import androidx.lifecycle.LiveData
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsStatus
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsSummary
import ltd.royalgreen.pacecloud.dashboardmodule.UserActivityLog
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface ApiService {
//    @GET("users/{login}")
//    fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>
//
//    @GET("users/{login}/repos")
//    fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>
//
//    @GET("repos/{owner}/{name}")
//    fun getRepo(
//        @Path("owner") owner: String,
//        @Path("name") name: String
//    ): LiveData<ApiResponse<Repo>>
//
//    @GET("repos/{owner}/{name}/contributors")
//    fun getContributors(
//        @Path("owner") owner: String,
//        @Path("name") name: String
//    ): LiveData<ApiResponse<List<Contributor>>>

//    @GET("/api/portal/loginportalusers")
//    fun loginToPaceCloudByLiveData(@Query("param") param: String): LiveData<ApiResponse<LoggedUser>>

    //API FOR LOGIN
    @GET("/api/portal/loginportalusers")
    fun loginportalusers(@Query("param") param: String): Call<LoggedUser>

    //API FOR USER BALANCE
    @GET("/api/portal/billclouduserbalance")
    fun billclouduserbalance(@Query("param") param: String): Call<BalanceModel>

    //API FOR USER VM STATUS
    @GET("/api/portal/GetDashboardChartPortal")
    fun GetDashboardChartPortal(@Query("param") param: String): Call<DashOsStatus>

    //API FOR USER VM SUMMERY
    @GET("/api/portal/GetDashboardChartPortal")
    fun GetDashboardChartPortalSummery(@Query("param") param: String): Call<DashOsSummary>

    //API FOR USER VM SUMMERY
    @GET("/api/portal/cloudactivitylog")
    fun cloudactivitylog(@Query("param") param: String): Call<UserActivityLog>
}
