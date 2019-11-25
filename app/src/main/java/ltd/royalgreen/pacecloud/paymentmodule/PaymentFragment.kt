package ltd.royalgreen.pacecloud.paymentmodule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.PaymentFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PaymentFragment : Fragment(), Injectable, PaymentRechargeDialog.RechargeCallback, RechargeConfirmDialog.RechargeConfirmCallback {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    lateinit var bottomSheeetBehaviour: BottomSheetBehavior<View>

    private val viewModel: PaymentFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(PaymentFragmentViewModel::class.java)
    }

    //For Payment History
    private lateinit var adapter: PaymentListAdapter

    private var binding by autoCleared<PaymentFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
                override fun onYes() {
                    preferences.edit().apply {
                        putString("LoggedUser", "")
                        apply()
                    }
                    requireActivity().finish()
                }
            }, "Do you want to exit?", "")
            exitDialog.show(parentFragmentManager, "#app_exit_dialog")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.payment_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheeetBehaviour = BottomSheetBehavior.from(binding.includedBottomSheet.bottomSheet)
        binding.searchFab.setOnClickListener{
            if (bottomSheeetBehaviour.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheeetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_black_24dp, activity!!.theme))
            } else {
                bottomSheeetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED)
                searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
            }
        }

        binding.includedContentMain.rechargeButton.setOnClickListener {
            val action = PaymentFragmentDirections.actionPaymentScreenToPaymentFosterWebViewFragment("")
            findNavController().navigate(action)
//            showRechargeDialog()
        }

        binding.includedBottomSheet.applyFilter.setOnClickListener {
            applySearch()
        }

        viewModel.showMessage.observe(this, Observer { (type, message) ->
            val parent: ViewGroup? = null
            val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (type == "SUCCESS") {
                val toastView = inflater.inflate(R.layout.toast_custom_green, parent)
                toastView.message.text = message
                toast.view = toastView
                toast.show()
            } else if (type == "ERROR") {
                val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                toastView.message.text = message
                toast.view = toastView
                toast.show()
            }
        })

        val paymentStatus = preferences.getString("paymentRechargeStatus", null)
        paymentStatus?.let {
            if (it == "true") {

                if (isNetworkAvailable(requireContext())) {

                    val jsonObject = JsonObject().apply {
                        addProperty("statusCheckUrl", "https://demo.fosterpayments.com.bd/fosterpayments/TransactionStatus/txstatus.php?mcnt_TxnNo=Txn522&mcnt_SecureHashValue=087a0abb04d51c84a952231db8fd5f69")
                    }

                    val param = JsonArray().apply {
                        add(jsonObject)
                    }

                    val handler = CoroutineExceptionHandler { _, exception ->
                        exception.printStackTrace()
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }

                    CoroutineScope(Dispatchers.IO).launch(handler) {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
                        val response = apiService.cloudrechargesave(param)
                        when (val apiResponse = ApiResponse.create(response)) {
                            is ApiSuccessResponse -> {
                                viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                                val rechargeStatusFosterResponse = apiResponse.body
                                if (rechargeStatusFosterResponse.resdata.resstate) {
                                    saveNewRecharge(rechargeStatusFosterResponse.resdata.fosterRes)
                                } else {
                                    viewModel.showMessage.postValue(Pair("ERROR", "Payment not successful !"))
                                }
                            }
                            is ApiEmptyResponse -> {
                                viewModel.apiCallStatus.postValue(ApiCallStatus.EMPTY)
                            }
                            is ApiErrorResponse -> {
                                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                            }
                        }
                    }
                } else {
                    val parent: ViewGroup? = null
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                    toastView.message.text = requireContext().getString(R.string.net_error_msg)
                    toast.view = toastView
                    toast.show()
                }
            } else if (it == "false"){
                val parent: ViewGroup? = null
                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                toastView.message.text = "Payment not successful !"
                toast.view = toastView
                toast.show()
            }

            preferences.edit().apply {
                putString("paymentRechargeStatus", "null")
                apply()
            }
        }

//        binding.includedBottomSheet.reset.setOnClickListener {
//            viewModel.paymentList.value?.dataSource?.invalidate()
//            viewModel.fromDate.value = "dd/mm/yyyy"
//            viewModel.toDate.value = "dd/mm/yyyy"
//            viewModel.searchValue.value = ""
//            if (bottomSheeetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
//                bottomSheeetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
//                searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
//            }
//        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.includedBottomSheet.viewModel = viewModel
        binding.includedContentMain.viewModel = viewModel

        adapter = PaymentListAdapter()

        binding.includedContentMain.paymentRecycler.layoutManager = LinearLayoutManager(activity)
        binding.includedContentMain.paymentRecycler.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        binding.includedContentMain.paymentRecycler.adapter = adapter

        //1
//        val config = PagedList.Config.Builder()
//            .setPageSize(30)
//            .setEnablePlaceholders(false)
//            .build()

        //2
//        viewModel.paymentList = viewModel.initializedPagedListBuilder(config).build()

        //3
//        viewModel.paymentList.observe(this, Observer<PagedList<BilCloudUserLedger>> { pagedList ->
//            adapter.submitList(pagedList)
//        })

        viewModel.lastRechargeResponse.observe(this, Observer { lastRecharge ->
            lastRecharge.resdata?.objBilCloudUserLedger?.let {
                viewModel.lastPaymentAmount.postValue(BigDecimal(it.creditAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString())
                val date = it.transactionDate
                if (date != null && date.contains("T")) {
                    val tempStringArray = date.split("T")
                    var tempString1 = tempStringArray[1]
                    if (tempString1.contains(".")){
                        tempString1 = tempString1.split(".")[0]
                        tempString1 = when {
                            tempString1.split(":")[0].toInt()>12 -> {
                                val hour = tempString1.split(":")[0].toInt()
                                val minute = tempString1.split(":")[1].toInt()
                                val seconds = tempString1.split(":")[2].toInt()
                                "${hour-12}:$minute:$seconds PM"
                            }
                            tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                            else -> "$tempString1 AM"
                        }
                    } else {
                        tempString1 = when {
                            tempString1.split(":")[0].toInt()>12 -> {
                                val hour = tempString1.split(":")[0].toInt()
                                val minute = tempString1.split(":")[1].toInt()
                                val seconds = tempString1.split(":")[2].toInt()
                                "${hour-12}:$minute:$seconds PM"
                            }
                            tempString1.split(":")[0].toInt()==12 -> "$tempString1 PM"
                            else -> "$tempString1 AM"
                        }
                    }
                    val year = tempStringArray[0].split("-")[0]
                    val month = tempStringArray[0].split("-")[1]
                    val day = tempStringArray[0].split("-")[2]
                    viewModel.lastPaymentDate.postValue("$day-$month-$year  |  $tempString1")
                }
            }
        })

        viewModel.apiCallStatus.observe(this, Observer<ApiCallStatus> { status ->
            val parent: ViewGroup? = null
            when(status) {
                ApiCallStatus.SUCCESS -> {
//                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
//                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_green, null)
//                    toastView.message.text = requireContext().getString(R.string.success_msg)
//                    toast.view = toastView
//                    toast.show()
                }
                ApiCallStatus.ERROR -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                    toastView.message.text = requireContext().getString(R.string.error_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.NO_DATA -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                    toastView.message.text = requireContext().getString(R.string.no_data_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.EMPTY -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                    toastView.message.text = requireContext().getString(R.string.empty_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.TIMEOUT -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                    toastView.message.text = requireContext().getString(R.string.timeout_msg)
                    toast.view = toastView
                    toast.show()
                }
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            viewModel.getUserBalance(it)
            viewModel.getLastRechargeBalance(it)
        }
    }

    private fun applySearch() {
//        viewModel.paymentList.value?.dataSource?.invalidate()
//        if (bottomSheeetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheeetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
//            searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, requireContext().theme))
//        }
    }

    override fun onSavePressed(date: String, amount: String, note: String) {
        if (isNetworkAvailable(requireContext())) {
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject()
            user?.let {
                jsonObject.addProperty("UserID", user.resdata?.loggeduser?.userID)
                jsonObject.addProperty("rechargeAmount", amount)
//                jsonObject.addProperty("Particulars", note)
//                jsonObject.addProperty("IsActive", true)
            }
            val param = JsonArray().apply {
                add(jsonObject)
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                exception.printStackTrace()
                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.cloudrecharge(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        val rechargeResponse = apiResponse.body
                        if (rechargeResponse.resdata?.resstate == true) {
                            showRechargeConfirmDialog(rechargeResponse)
                            preferences.edit().apply {
                                putString("paymentStatusUrl", rechargeResponse.resdata.paymentStatusUrl)
                                apply()
                            }

                            viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                            user?.let {
                                viewModel.getUserBalance(it)
                                viewModel.getLastRechargeBalance(it)
                            }
//                            viewModel.paymentList.value?.dataSource?.invalidate()
                        } else {
                            viewModel.apiCallStatus.postValue(ApiCallStatus.NO_DATA)
                        }
                    }
                    is ApiEmptyResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            val parent: ViewGroup? = null
            val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
            toastView.message.text = requireContext().getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

    private fun refreshUI() {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            viewModel.getUserBalance(it)
            viewModel.getLastRechargeBalance(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh -> {
                refreshUI()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showRechargeDialog() {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            val rechargeDialog = PaymentRechargeDialog(this, it.resdata?.loggeduser?.fullName)
            rechargeDialog.isCancelable = false
            rechargeDialog.show(parentFragmentManager, "#recharge_dialog")
        }
    }

    private fun showRechargeConfirmDialog(rechargeResponse: RechargeResponse) {
        val rechargeConfirmDialog = RechargeConfirmDialog(this, rechargeResponse.resdata?.amount, rechargeResponse.resdata?.paymentProcessUrl)
        rechargeConfirmDialog.isCancelable = false
        rechargeConfirmDialog.show(parentFragmentManager, "#recharge_confirm_dialog")
    }

    override fun onClicked(url: String?) {
        viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
        val action = PaymentFragmentDirections.actionPaymentScreenToPaymentFosterWebViewFragment(url)
        findNavController().navigate(action)
    }

    private fun saveNewRecharge(fosterString: String) {

        if (isNetworkAvailable(requireContext())) {
            val fosterJsonObject = JsonParser().parse(fosterString).asJsonArray[0].asJsonObject
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
                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
                val response = apiService.newrechargesave(param)
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val rechargeFinalSaveResponse = apiResponse.body
                        if (rechargeFinalSaveResponse.resdata.resstate == true) {
                            viewModel.showMessage.postValue(Pair("SUCCESS", rechargeFinalSaveResponse.resdata.message))
                            refreshUI()
                        } else {
                            viewModel.showMessage.postValue(Pair("ERROR", rechargeFinalSaveResponse.resdata.message))
                        }
                    }
                    is ApiEmptyResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            val parent: ViewGroup? = null
            val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
            toastView.message.text = requireContext().getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }
}