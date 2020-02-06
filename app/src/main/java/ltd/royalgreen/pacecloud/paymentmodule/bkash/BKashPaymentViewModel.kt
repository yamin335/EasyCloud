package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ltd.royalgreen.pacecloud.mainactivitymodule.BaseViewModel
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.PaymentRepository
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BKashPaymentViewModel @Inject constructor(private val application: Application, private val repository: PaymentRepository) : BaseViewModel() {

    val resBkash: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val bKashPaymentStatus: MutableLiveData<Pair<Boolean, String>> by lazy {
        MutableLiveData<Pair<Boolean, String>>()
    }

    var bkashPaymentExecuteJson = JsonObject()

    var bkashToken: String? = ""

    fun createBkashCheckout(paymentRequest: PaymentRequest?, createBkash: CreateBkashModel?) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashCreatePaymentRepo(paymentRequest, createBkash))) {
                    is ApiSuccessResponse -> {
                        apiCallStatus.postValue("SUCCESS")
                        val paymentCreateResponse = apiResponse.body
                        if (paymentCreateResponse.resdata?.resbKash != null) {
                            apiCallStatus.postValue("SUCCESS")
                            resBkash.postValue(paymentCreateResponse.resdata.resbKash)
                        } else {
                            apiCallStatus.postValue("NO_DATA")
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

    fun executeBkashPayment() {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashExecutePaymentRepo(bkashPaymentExecuteJson, bkashToken))) {
                    is ApiSuccessResponse -> {
                        val paymentExecuteResponse = apiResponse.body
                        if (paymentExecuteResponse.resdata?.resExecuteBk != null) {
                            apiCallStatus.postValue("SUCCESS")
                            saveBkashNewRecharge(paymentExecuteResponse.resdata.resExecuteBk)
                        } else {
                            apiCallStatus.postValue("NO_DATA")
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

    fun saveBkashNewRecharge(bkashPaymentResponse: String) {
        if (checkNetworkStatus(application)) {
            apiCallStatus.postValue("LOADING")
            viewModelScope.launch {
                when (val apiResponse = ApiResponse.create(repository.bkashPaymentSaveRepo(bkashPaymentResponse))) {
                    is ApiSuccessResponse -> {
                        val rechargeFinalSaveResponse = apiResponse.body
                        bKashPaymentStatus.postValue(Pair(rechargeFinalSaveResponse.resdata.resstate ?: false, rechargeFinalSaveResponse.resdata.message))
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
}