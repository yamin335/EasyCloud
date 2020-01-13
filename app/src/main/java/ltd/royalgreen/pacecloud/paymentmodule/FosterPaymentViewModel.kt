package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FosterPaymentViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val rechargeSuccessFailureStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val showMessage: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    fun checkFosterPaymentStatus(fosterPaymentStatusUrl: String) {
        if (isNetworkAvailable(application)) {

            val jsonObject = JsonObject().apply {
                addProperty("statusCheckUrl", fosterPaymentStatusUrl)
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.cloudrechargesave(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val rechargeStatusFosterResponse = apiResponse.body
                        if (rechargeStatusFosterResponse.resdata.resstate) {
                            saveNewRecharge(rechargeStatusFosterResponse.resdata.fosterRes)
                        } else {
                            showMessage.postValue(Pair("ERROR", "Payment not successful !"))
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
    }

    private fun saveNewRecharge(fosterString: String) {

        if (isNetworkAvailable(application)) {
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

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.newrechargesave(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val rechargeFinalSaveResponse = apiResponse.body
                        if (rechargeFinalSaveResponse.resdata.resstate == true) {
                            showMessage.postValue(Pair("SUCCESS", rechargeFinalSaveResponse.resdata.message))
                            rechargeSuccessFailureStatus.postValue(rechargeFinalSaveResponse.resdata.resstate)
                        } else {
                            showMessage.postValue(Pair("ERROR", rechargeFinalSaveResponse.resdata.message))
                        }
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
    }
}