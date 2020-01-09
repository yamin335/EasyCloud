package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.FosterModel
import ltd.royalgreen.pacecloud.paymentmodule.TModel
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BKashPaymentFragmentViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val resBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val bKashPaymentStatus: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    var bkashPaymentExecuteJson = JsonObject()

    var bkashToken: String? = ""

    fun createBkashCheckout(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?) {
        if (isNetworkAvailable(application)) {
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

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.createbkashpayment(body)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val paymentCreateResponse = apiResponse.body
                        if (paymentCreateResponse.resdata?.resstate == true) {
                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                            resBkash.postValue(paymentCreateResponse.resdata.resbKash)
                        } else {
                            apiCallStatus.postValue(ApiCallStatus.NO_DATA)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

    fun executeBkashPayment() {
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("authToken", bkashToken)
                addProperty("paymentID", bkashPaymentExecuteJson.get("paymentID").asString)
            }

            val body = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.executebkashpayment(body)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val paymentExecuteResponse = apiResponse.body
                        if (paymentExecuteResponse.resdata?.resstate == true) {
                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                            saveBkashNewRecharge(paymentExecuteResponse.resdata.resExecuteBk)
                        } else {
                            apiCallStatus.postValue(ApiCallStatus.NO_DATA)
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

    fun saveBkashNewRecharge(bkashPaymentResponse: String?) {
        if (isNetworkAvailable(application)) {
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

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.newrechargebkashpayment(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val rechargeFinalSaveResponse = apiResponse.body
                        bKashPaymentStatus.postValue(Pair(rechargeFinalSaveResponse.resdata.resstate ?: false, rechargeFinalSaveResponse.resdata.message))
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
            val toast = Toast.makeText(application, "", Toast.LENGTH_LONG)
            val inflater = application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, null)
            toastView.message.text = application.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }
}