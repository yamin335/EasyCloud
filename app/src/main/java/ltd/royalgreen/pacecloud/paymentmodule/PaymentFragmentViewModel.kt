package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.bkash.CreateBkashModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.PaymentRequest
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject

class PaymentFragmentViewModel @Inject constructor(app: Application) : ViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiService: ApiService

    val application = app

    val apiCallStatus: MutableLiveData<ApiCallStatus> by lazy {
        MutableLiveData<ApiCallStatus>()
    }

    val lastRechargeResponse: MutableLiveData<LastRechargeBalance> by lazy {
        MutableLiveData<LastRechargeBalance>()
    }

    val fromDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastPaymentAmount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastPaymentDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val toDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val searchValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val userBalance: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var hasBkashToken = false

    val bKashToken: MutableLiveData<Pair<PaymentRequest, CreateBkashModel>> by lazy {
        MutableLiveData<Pair<PaymentRequest, CreateBkashModel>>()
    }

//    lateinit var paymentList: LiveData<PagedList<BilCloudUserLedger>>

    init {
        fromDate.value = "dd/mm/yyyy"
        toDate.value = "dd/mm/yyyy"
        searchValue.value = ""
    }

    val fosterUrl: MutableLiveData<Pair<String, String>> by lazy {
        MutableLiveData<Pair<String, String>>()
    }

    fun getFosterPaymentUrl(amount: String, note: String) {
        if (isNetworkAvailable(application)) {
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

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.cloudrecharge(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body
                        if (rechargeResponse.resdata?.resstate == true) {
//                            preferences.edit().apply {
//                                putString("paymentStatusUrl", rechargeResponse.resdata.paymentStatusUrl)
//                                apply()
//                            }

                            fosterUrl.postValue(Pair(rechargeResponse.resdata.paymentProcessUrl ?: "", rechargeResponse.resdata.paymentStatusUrl ?: ""))

                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
    }

    fun getBkashToken(amount: String) {
        if (isNetworkAvailable(application)) {

            val jsonObject = JsonObject().apply {
                addProperty("id", 0)
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.generatebkashtoken(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val bKashTokenResponse = apiResponse.body
                        if (bKashTokenResponse.resdata?.resstate == true && bKashTokenResponse.resdata.tModel != null) {
                            val paymentRequest = PaymentRequest()
                            paymentRequest.amount = amount

                            val createBkashModel = CreateBkashModel()
                            createBkashModel.authToken = JsonParser.parseString(bKashTokenResponse.resdata.tModel.token).asJsonObject.get("id_token").asString
                            createBkashModel.rechargeAmount = amount
                            createBkashModel.currency = bKashTokenResponse.resdata.tModel.currency
                            createBkashModel.mrcntNumber = bKashTokenResponse.resdata.tModel.marchantInvNo

                            hasBkashToken = true
                            bKashToken.postValue(Pair(paymentRequest, createBkashModel))
                            apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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
            showErrorToast(application, application.getString(R.string.net_error_msg))
        }
    }

    fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Long, BilCloudUserLedger> {
        val dataSourceFactory = object : DataSource.Factory<Long, BilCloudUserLedger>() {
            override fun create(): DataSource<Long, BilCloudUserLedger> {
                val jsonObject = JsonObject().apply {
                    addProperty("values", searchValue.value)
                    if (fromDate.value != "dd/mm/yyyy" && toDate.value != "dd/mm/yyyy") {
                        addProperty("SDate", fromDate.value)
                        addProperty("EDate", toDate.value)
                    } else {
                        addProperty("SDate", "")
                        addProperty("EDate", "")
                    }
                }
                return PaymentListDataSource(apiService, preferences, apiCallStatus, jsonObject)
            }
        }
        return LivePagedListBuilder<Long, BilCloudUserLedger>(dataSourceFactory, config)
    }

//    fun getBkashTokenAndFosterUrl(): MediatorLiveData<BkashTokenAndFosterUrlMergedData> {
//        val mergedData = MediatorLiveData<BkashTokenAndFosterUrlMergedData>()
//        mergedData.addSource(bKashToken) {
//            if (it != null) {
//                mergedData.value = BkashTokenData(it)
//            }
//        }
//
//        mergedData.addSource(fosterUrl) {
//            if (it != null) {
//                mergedData.value = FosterUrlData(it)
//            }
//        }
//
//        return mergedData
//    }

    fun getUserBalance(user: LoggedUser?) {
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user?.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.billclouduserbalance(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val balanceModel = apiResponse.body
                        userBalance.postValue(BigDecimal(balanceModel.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString())
                        val userBalanceSerialized = Gson().toJson(apiResponse.body)
                        preferences.edit().apply {
                            putString("UserBalance", userBalanceSerialized)
                            apply()
                        }
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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

    fun getLastRechargeBalance(user: LoggedUser?) {
        if (isNetworkAvailable(application)) {
            val jsonObject = JsonObject().apply {
                addProperty("UserId", user?.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.lastbillbyuser(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        lastRechargeResponse.postValue(apiResponse.body)
                        apiCallStatus.postValue(ApiCallStatus.SUCCESS)
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

    fun pickDate(view: View){
        // calender class's instance and get current date , month and year from calender
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR) // current year
        val mMonth = c.get(Calendar.MONTH) // current month
        val mDay = c.get(Calendar.DAY_OF_MONTH) // current day
        // date picker dialog
        val datePickerDialog = DatePickerDialog(view.context,
            { _, year, monthOfYear, dayOfMonth ->
                when(view.id) {
                    R.id.fromDate -> {
                        fromDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                    R.id.toDate -> {
                        toDate.value = year.toString()+"-"+(monthOfYear + 1)+"-"+dayOfMonth
                    }
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
}