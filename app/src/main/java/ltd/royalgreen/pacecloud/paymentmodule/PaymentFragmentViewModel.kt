package ltd.royalgreen.pacecloud.paymentmodule

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
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

    val paymentResponse: MutableLiveData<PaymentHistory> by lazy {
        MutableLiveData<PaymentHistory>()
    }

    val fromDate: MutableLiveData<String> by lazy {
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

    lateinit var paymentList: LiveData<PagedList<BilCloudUserLedger>>

    init {
        fromDate.value = "dd/mm/yyyy"
        toDate.value = "dd/mm/yyyy"
        searchValue.value = ""
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
                return PaymentListDataSource(application, apiService, preferences, paymentResponse, apiCallStatus, jsonObject)
            }
        }
        return LivePagedListBuilder<Long, BilCloudUserLedger>(dataSourceFactory, config)
    }

    fun getUserBalance(user: LoggedUser?) {
        if (isNetworkAvailable(application)) {
            apiCallStatus.postValue(ApiCallStatus.LOADING)
            val jsonObject = JsonObject().apply {
                addProperty("UserID", user?.resdata?.loggeduser?.userID)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }.toString()

            val handler = CoroutineExceptionHandler { _, exception ->
                apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                withTimeoutOrNull(3000L) {
                    val response = apiService.billclouduserbalance(param).execute()
                    val apiResponse = ApiResponse.create(response)
                    when (apiResponse) {
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
            }
        } else {
            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
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