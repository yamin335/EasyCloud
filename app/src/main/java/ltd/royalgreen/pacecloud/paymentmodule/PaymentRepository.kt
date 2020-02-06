package ltd.royalgreen.pacecloud.paymentmodule

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.DefaultResponse
import ltd.royalgreen.pacecloud.network.ApiService
import ltd.royalgreen.pacecloud.paymentmodule.bkash.CreateBkashModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.PaymentRequest
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(private val apiService: ApiService,
                                            private val preferences: SharedPreferences
) {

    suspend fun usrBalanceRepo(): Response<BalanceModel> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("UserID", user?.resdata?.loggeduser?.userID)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.billclouduserbalance(param)
        }
    }

    suspend fun usrLastRechargeRepo(): Response<LastRechargeBalance> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("UserId", user?.resdata?.loggeduser?.userID)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.lastbillbyuser(param)
        }
    }

    suspend fun fosterUrlRepo(amount: String, note: String): Response<RechargeResponse> {

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject()
        user?.let {
            jsonObject.addProperty("UserID", user.resdata?.loggeduser?.userID)
            jsonObject.addProperty("rechargeAmount", amount)
            jsonObject.addProperty("Particulars", note)
            jsonObject.addProperty("IsActive", true)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.cloudrecharge(param)
        }
    }

    suspend fun fosterStatusRepo(fosterPaymentStatusUrl: String): Response<RechargeStatusFosterCheckModel> {

        val jsonObject = JsonObject().apply {
            addProperty("statusCheckUrl", fosterPaymentStatusUrl)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.cloudrechargesave(param)
        }
    }

    suspend fun fosterRechargeSaveRepo(fosterString: String): Response<DefaultResponse> {

        val fosterJsonObject = JsonParser.parseString(fosterString).asJsonArray[0].asJsonObject
        val fosterModel = Gson().fromJson(fosterJsonObject, FosterModel::class.java)
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("CloudUserID", user?.resdata?.loggeduser?.userID)
            addProperty("UserTypeId", user?.resdata?.loggeduser?.userType)
            addProperty("TransactionNo", fosterModel.MerchantTxnNo)
            addProperty("InvoiceId", 0)
            addProperty("UserName", user?.resdata?.loggeduser?.displayName)
            addProperty("TransactionDate", today)
            addProperty("RechargeType", "foster")
            addProperty("BalanceAmount", fosterModel.TxnAmount)
            addProperty("Particulars", "")
            addProperty("IsActive", true)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(fosterJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargesave(param)
        }
    }

    suspend fun bkashTokenRepo(amount: String): Response<BKashTokenResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("id", 0)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.generatebkashtoken(param)
        }
    }

    suspend fun bkashCreatePaymentRepo(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?): Response<BKashCreatePaymentResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("authToken", createBkash?.authToken)
            addProperty("rechargeAmount", createBkash?.rechargeAmount)
            addProperty("Name", paymentRequest?.intent)
            addProperty("currency", createBkash?.currency)
            addProperty("mrcntNumber", createBkash?.mrcntNumber)
        }

        val body = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.createbkashpayment(body)
        }
    }

    suspend fun bkashExecutePaymentRepo(bkashPaymentExecuteJson: JsonObject, bkashToken: String?): Response<BKashExecutePaymentResponse> {

        val jsonObject = JsonObject().apply {
            addProperty("authToken", bkashToken)
            addProperty("paymentID", bkashPaymentExecuteJson.get("paymentID").asString)
        }

        val body = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.executebkashpayment(body)
        }
    }

    suspend fun bkashPaymentSaveRepo(bkashPaymentResponse: String): Response<DefaultResponse> {
        val bkashJsonObject = JsonParser.parseString(bkashPaymentResponse).asJsonObject
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        //Current Date
        val todayInMilSec = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val today = df.format(todayInMilSec)
        val jsonObject = JsonObject().apply {
            addProperty("CloudUserID", user?.resdata?.loggeduser?.userID)
            addProperty("UserTypeId", user?.resdata?.loggeduser?.userType)
            addProperty("TransactionNo", bkashJsonObject.get("trxID").asString)
            addProperty("InvoiceId", 0)
            addProperty("UserName", user?.resdata?.loggeduser?.displayName)
            addProperty("TransactionDate", today)
            addProperty("RechargeType", "bKash")
            addProperty("BalanceAmount", bkashJsonObject.get("amount").asString)
            addProperty("Particulars", "")
            addProperty("IsActive", true)
        }

        val param = JsonArray().apply {
            add(jsonObject)
            add(bkashJsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.newrechargebkashpayment(param)
        }
    }
}