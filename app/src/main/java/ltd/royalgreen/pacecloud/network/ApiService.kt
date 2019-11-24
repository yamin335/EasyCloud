package ltd.royalgreen.pacecloud.network

import com.google.gson.JsonArray
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsStatus
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsSummary
import ltd.royalgreen.pacecloud.dashboardmodule.UserActivityLog
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.DefaultResponse
import ltd.royalgreen.pacecloud.paymentmodule.LastRechargeBalance
import ltd.royalgreen.pacecloud.paymentmodule.PaymentHistory
import ltd.royalgreen.pacecloud.paymentmodule.RechargeResponse
import ltd.royalgreen.pacecloud.paymentmodule.RechargeStatusFosterCheckModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface ApiService {
    //API FOR LOGIN
    @GET("/api/portal/loginportalusers")
    fun loginportalusers(@Query("param") param: String): Call<LoggedUser>

    //API FOR USER BALANCE
    @GET("/api/portal/billclouduserbalance")
    fun billclouduserbalance(@Query("param") param: String): Call<BalanceModel>

    //API FOR USER VM STATUS
    @GET("/api/portal/GetDashboardChartPortal")
    suspend fun getDashboardChartPortal(@Query("param") param: String): Response<DashOsStatus>

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

    //API FOR SYNC DATABASE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/clouduservmsyncwithlocaldb")
    fun clouduservmsyncwithlocaldb(@Body jsonArray: JsonArray): Call<String>

    //API FOR SIGN UP
    @Headers("Content-Type: application/json")
    @POST("/api/portal/register")
    fun register(@Body jsonArray: JsonArray): Call<String>

    //API FOR RECHARGE
    @Headers("Content-Type: application/json")
    @POST("/api/billclouduserclient/cloudrecharge")
    suspend fun cloudrecharge(@Body jsonArray: JsonArray): Response<RechargeResponse>

    //API FOR RECHARGE STATUS CHECK
    @Headers("Content-Type: application/json")
    @POST("/api/billclouduserclient/cloudrechargesave")
    suspend fun cloudrechargesave(@Body jsonArray: JsonArray): Response<RechargeStatusFosterCheckModel>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/newrechargesave")
    suspend fun newrechargesave(@Body jsonArray: JsonArray): Response<DefaultResponse>



}
