package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Application
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BkashDataModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.CreateBkashModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.PaymentRequest
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject

class PaymentFragmentViewModel @Inject constructor(private val application: Application, private val repository: PaymentRepository) : BaseViewModel() {
    @Inject
    lateinit var preferences: SharedPreferences

    val lastRechargeResponse: MutableLiveData<LastRechargeBalance> by lazy {
        MutableLiveData<LastRechargeBalance>()
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

    val fromDate: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val searchValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val userBalance: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val bKashToken: MutableLiveData<BkashDataModel> by lazy {
        MutableLiveData<BkashDataModel>()
    }

    var hasBkashToken = false

//    lateinit var paymentList: LiveData<PagedList<BilCloudUserLedger>>

    init {
        fromDate.value = "dd/mm/yyyy"
        toDate.value = "dd/mm/yyyy"
        searchValue.value = ""
    }

    val fosterUrl: MutableLiveData<Pair<String?, String?>> by lazy {
        MutableLiveData<Pair<String?, String?>>()
    }

    fun getFosterPaymentUrl(amount: String, note: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.fosterUrlRepo(amount, note))) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body
                        if (rechargeResponse.resdata != null) {
//                            preferences.edit().apply {
//                                putString("paymentStatusUrl", rechargeResponse.resdata.paymentStatusUrl)
//                                apply()
//                            }

                            fosterUrl.postValue(Pair(rechargeResponse.resdata.paymentProcessUrl, rechargeResponse.resdata.paymentStatusUrl))
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    fun getBkashToken(amount: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashTokenRepo(amount))) {
                    is ApiSuccessResponse -> {
                        val bKashTokenResponse = apiResponse.body
                        if (bKashTokenResponse.resdata?.tModel != null) {
                            hasBkashToken = true
                            val paymentRequest = PaymentRequest()
                            paymentRequest.amount = amount

                            val createBkashModel = CreateBkashModel()
                            createBkashModel.authToken = JsonParser.parseString(bKashTokenResponse.resdata.tModel.token).asJsonObject.get("id_token").asString
                            createBkashModel.rechargeAmount = amount
                            createBkashModel.currency = bKashTokenResponse.resdata.tModel.currency
                            createBkashModel.mrcntNumber = bKashTokenResponse.resdata.tModel.marchantInvNo

                            val bkashDataModel = BkashDataModel()
                            bkashDataModel.paymentRequest = paymentRequest
                            bkashDataModel.createBkashModel = createBkashModel
                            bKashToken.postValue(bkashDataModel)
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

//    fun initializedPagedListBuilder(config: PagedList.Config):
//            LivePagedListBuilder<Long, BilCloudUserLedger> {
//        val dataSourceFactory = object : DataSource.Factory<Long, BilCloudUserLedger>() {
//            override fun create(): DataSource<Long, BilCloudUserLedger> {
//                val jsonObject = JsonObject().apply {
//                    addProperty("values", searchValue.value)
//                    if (fromDate.value != "dd/mm/yyyy" && toDate.value != "dd/mm/yyyy") {
//                        addProperty("SDate", fromDate.value)
//                        addProperty("EDate", toDate.value)
//                    } else {
//                        addProperty("SDate", "")
//                        addProperty("EDate", "")
//                    }
//                }
//                return PaymentListDataSource(apiService, preferences, apiCallStatus, jsonObject)
//            }
//        }
//        return LivePagedListBuilder<Long, BilCloudUserLedger>(dataSourceFactory, config)
//    }

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

    fun getUserBalance() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.usrBalanceRepo())) {
                    is ApiSuccessResponse -> {
                        val balanceModel = apiResponse.body
                        userBalance.postValue(BigDecimal(balanceModel.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString())
                        val userBalanceSerialized = Gson().toJson(apiResponse.body)
                        preferences.edit().apply {
                            putString("UserBalance", userBalanceSerialized)
                            apply()
                        }
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
        }
    }

    fun getLastRechargeBalance() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.usrLastRechargeRepo())) {
                    is ApiSuccessResponse -> {
                        lastRechargeResponse.postValue(apiResponse.body)
                        apiCallStatus.postValue("SUCCESS")
                    }
                    is ApiEmptyResponse -> {
                        apiCallStatus.postValue("EMPTY")
                    }
                    is ApiErrorResponse -> {
                        apiCallStatus.postValue("ERROR")
                    }
                }
            }
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