package ltd.royalgreen.pacecloud.network

import com.google.gson.JsonArray
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsStatus
import ltd.royalgreen.pacecloud.dashboardmodule.DashOsSummary
import ltd.royalgreen.pacecloud.dashboardmodule.UserActivityLog
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.DefaultResponse
import ltd.royalgreen.pacecloud.paymentmodule.*
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface ApiService {
    //API FOR LOGIN
    @Headers("Content-Type: application/json")
    @POST("/api/portal/loginportalusers")
    suspend fun loginportalusers(@Body jsonArray: JsonArray): Response<LoggedUser>

    //API FOR USER BALANCE
    @GET("/api/portal/billclouduserbalance")
    suspend fun billclouduserbalance(@Query("param") param: String): Response<BalanceModel>

    //API FOR USER VM STATUS
    @GET("/api/portal/GetDashboardChartPortal")
    suspend fun getDashboardChartPortal(@Query("param") param: String): Response<DashOsStatus>

    //API FOR USER VM SUMMERY
    @GET("/api/portal/GetDashboardChartPortal")
    suspend fun getDashboardChartPortalSummery(@Query("param") param: String): Response<DashOsSummary>

    //API FOR USER ACTIVITY LOG
    @GET("/api/portal/cloudactivitylog")
    suspend fun cloudactivitylog(@Query("param") param: String): Response<UserActivityLog>

    //API FOR USER VM LIST
    @GET("/api/portal/cloudvmbyuserid")
    suspend fun cloudvmbyuserid(@Query("param") param: String): Response<String>

    //API FOR USER PAYMENT HISTORY
    @GET("/api/portal/billhistory")
    suspend fun billhistory(@Query("param") param: String): Response<PaymentHistory>

    //API FOR LAST PAYMENT AMOUNT
    @GET("/api/portal/lastbillbyuser")
    suspend fun lastbillbyuser(@Query("param") param: String): Response<LastRechargeBalance>

    //API FOR VM START_STOP
    @Headers("Content-Type: application/json")
    @POST("/api/portal/cloudvmstartstop")
    suspend fun cloudvmstartstop(@Body jsonArray: JsonArray): Response<String>

    //API FOR VM REBOOT
    @Headers("Content-Type: application/json")
    @POST("/api/portal/cloudvmreboot")
    suspend fun cloudvmreboot(@Body jsonArray: JsonArray): Response<String>

    //API FOR RENAME DEPLOYMENT
    @Headers("Content-Type: application/json")
    @POST("/api/portal/updatedeploymentname")
    suspend fun updatedeploymentname(@Body jsonArray: JsonArray): Response<String>

    //API FOR DEPLOYMENT NOTE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/updatevmnote")
    suspend fun updatevmnote(@Body jsonArray: JsonArray): Response<String>

    //API FOR SYNC DATABASE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/clouduservmsyncwithlocaldb")
    suspend fun clouduservmsyncwithlocaldb(@Body jsonArray: JsonArray): Response<String>

    //API FOR SIGN UP
    @Headers("Content-Type: application/json")
    @POST("/api/portal/register")
    suspend fun register(@Body jsonArray: JsonArray): Response<String>

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

    //API FOR GENERATE TOKEN FOR BKASH PAYMENT
    @GET("/api/portal/generatebkashtoken")
    suspend fun generatebkashtoken(@Query("param") param: String): Response<BKashTokenResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/createbkashpayment")
    suspend fun createbkashpayment(@Body jsonArray: JsonArray): Response<BKashCreatePaymentResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/executebkashpayment")
    suspend fun executebkashpayment(@Body jsonArray: JsonArray): Response<BKashExecutePaymentResponse>

    //API FOR RECHARGE SAVE
    @Headers("Content-Type: application/json")
    @POST("/api/portal/newrechargebkashpayment")
    suspend fun newrechargebkashpayment(@Body jsonArray: JsonArray): Response<DefaultResponse>
}
