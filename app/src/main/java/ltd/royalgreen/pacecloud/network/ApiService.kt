package ltd.royalgreen.pacecloud.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsStatus
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsSummary
import ltd.royalgreen.pacecloud.dashboardmodule.UserActivityLog
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.paymentmodule.LastRechargeBalance
import ltd.royalgreen.pacecloud.paymentmodule.PaymentHistory
import ltd.royalgreen.pacecloud.paymentmodule.RechargeResponse
import retrofit2.Call
import retrofit2.http.*

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
    fun getDashboardChartPortal(@Query("param") param: String): Call<DashOsStatus>

    //API FOR USER VM SUMMERY
    @GET("/api/portal/GetDashboardChartPortal")
    fun getDashboardChartPortalSummery(@Query("param") param: String): Call<DashOsSummary>

    //API FOR USER ACTIVITY LOG
    @GET("/api/portal/cloudactivitylog")
    fun cloudactivitylog(@Query("param") param: String): Call<UserActivityLog>

    //API FOR USER VM LIST
    @GET("/api/portal/cloudvmbyuserid")
    fun cloudvmbyuserid(@Query("param") param: String): Call<String>

    //API FOR USER PAYMENT HISTORY
    @GET("/api/portal/billhistory")
    fun billhistory(@Query("param") param: String): Call<PaymentHistory>

    //API FOR LAST PAYMENT AMOUNT
    @GET("/api/portal/lastbillbyuser")
    fun lastbillbyuser(@Query("param") param: String): Call<LastRechargeBalance>

    //API FOR VM START_STOP
    @Headers("Content-Type: application/json")
    @POST("/api/portal/cloudvmstartstop")
    fun cloudvmstartstop(@Body jsonArray: JsonArray): Call<String>

    //API FOR VM REBOOT
    @Headers("Content-Type: application/json")
    @POST("/api/portal/cloudvmreboot")
    fun cloudvmreboot(@Body jsonArray: JsonArray): Call<String>

    //API FOR RENAME DEPLOYMENT
    @Headers("Content-Type: application/json")
        @POST("/api/portal/updatedeploymentname")
    fun updatedeploymentname(@Body jsonArray: JsonArray): Call<String>

    //API FOR DEPLOYMENT NOTE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/updatevmnote")
    fun updatevmnote(@Body jsonArray: JsonArray): Call<String>

    //API FOR RECHARGE
    @POST("/api/portal/newrechargesave")
    fun newrechargesave(@Body jsonArray: JsonArray): Call<RechargeResponse>

    //API FOR SYNC DATABASE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/clouduservmsyncwithlocaldb")
    fun clouduservmsyncwithlocaldb(@Body jsonArray: JsonArray): Call<String>

    //API FOR SIGN UP
    @Headers("Content-Type: application/json")
    @POST("/api/portal/register")
    fun register(@Body jsonArray: JsonArray): Call<String>
}
