package ltd.royalgreen.pacecloud.paymentmodule

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.payment_fragment.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.PaymentFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacecloud.paymentmodule.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacecloud.util.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class PaymentFragment : Fragment(), Injectable, PaymentRechargeDialog.RechargeCallback, RechargeConfirmDialog.RechargeConfirmCallback, BKashPaymentWebDialog.BkashPaymentCallback,
    FosterPaymentWebDialog.FosterPaymentCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    lateinit var bottomSheetBehaviour: BottomSheetBehavior<View>

    private val viewModel: PaymentFragmentViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.includedBottomSheet.viewModel = viewModel
        binding.includedContentMain.viewModel = viewModel

        bottomSheetBehaviour = BottomSheetBehavior.from(binding.includedBottomSheet.bottomSheet)
        binding.searchFab.setOnClickListener{
            if (bottomSheetBehaviour.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_black_24dp, activity!!.theme))
            } else {
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED)
                searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
            }
        }

        binding.includedContentMain.rechargeButton.setOnClickListener {
//            val action = PaymentFragmentDirections.actionPaymentScreenToPaymentFosterWebViewFragment("")
//            findNavController().navigate(action)
            showRechargeDialog()
        }

        binding.includedBottomSheet.applyFilter.setOnClickListener {
            applySearch()
        }

        viewModel.bKashToken.observe(viewLifecycleOwner, Observer { bkashDataModel ->
            if (bkashDataModel != null) {
                val bkashPaymentDialog = BKashPaymentWebDialog(this, bkashDataModel.createBkashModel, bkashDataModel.paymentRequest)
                bkashPaymentDialog.isCancelable = false
                bkashPaymentDialog.show(parentFragmentManager, "#bkash_payment_dialog")
            }
        })

        viewModel.fosterUrl.observe(viewLifecycleOwner, Observer { (paymentProcessUrl, paymentStatusUrl) ->
            if (paymentProcessUrl != null && paymentStatusUrl != null) {
                val fosterPaymentDialog =
                    FosterPaymentWebDialog(
                        this,
                        paymentProcessUrl,
                        paymentStatusUrl
                    )
                fosterPaymentDialog.isCancelable = false
                fosterPaymentDialog.show(parentFragmentManager, "#foster_payment_dialog")
            }
        })

        val paymentStatus = preferences.getString("paymentRechargeStatus", null)
        paymentStatus?.let {
            if (it == "true") {


            } else if (it == "false"){
                showErrorToast(requireContext(), "Payment not successful !")
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

        viewModel.lastRechargeResponse.observe(viewLifecycleOwner, Observer { lastRecharge ->
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

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer<String> { status ->
            when(status) {
                "SUCCESS" -> {
//                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
//                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val toastView = inflater.inflate(R.layout.toast_custom_success, null)
//                    toastView.message.text = requireContext().getString(R.string.success_msg)
//                    toast.view = toastView
//                    toast.show()
                }
                "ERROR" -> {
                    showErrorToast(requireContext(), requireContext().getString(R.string.error_msg))
                }
                "NO_DATA" -> {
                    showWarningToast(requireContext(), requireContext().getString(R.string.no_data_msg))
                }
                "EMPTY" -> {
                    showWarningToast(requireContext(), requireContext().getString(R.string.empty_msg))
                }
                "TIMEOUT" -> {
                    showWarningToast(requireContext(), requireContext().getString(R.string.timeout_msg))
                }
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })

        viewModel.getUserBalance()
        viewModel.getLastRechargeBalance()
    }

    private fun applySearch() {
//        viewModel.paymentList.value?.dataSource?.invalidate()
//        if (bottomSheeetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheeetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
//            searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, requireContext().theme))
//        }
    }

    private fun refreshUI() {
        viewModel.getUserBalance()
        viewModel.getLastRechargeBalance()
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

    private fun showRechargeConfirmDialog(amount: String, note: String) {
//        val rechargeConfirmDialog = RechargeConfirmDialog(this, rechargeResponse?.resdata?.amount, note, rechargeResponse?.resdata?.paymentProcessUrl)
//        rechargeConfirmDialog.isCancelable = false
//        rechargeConfirmDialog.show(parentFragmentManager, "#recharge_confirm_dialog")

        val rechargeConfirmDialog = RechargeConfirmDialog(this, amount, note)
        rechargeConfirmDialog.isCancelable = false
        rechargeConfirmDialog.show(parentFragmentManager, "#recharge_confirm_dialog")
    }

    override fun onSavePressed(date: String, amount: String, note: String) {
        showRechargeConfirmDialog(amount, note)
    }

    override fun onFosterClicked(amount: String, note: String) {
        viewModel.getFosterPaymentUrl(amount, note)
    }

    override fun onBKashClicked(amount: String) {
        if (viewModel.hasBkashToken) {
            val bkashPaymentDialog = BKashPaymentWebDialog(this, viewModel.bKashToken.value?.createBkashModel!!, viewModel.bKashToken.value?.paymentRequest!!)
            bkashPaymentDialog.isCancelable = false
            bkashPaymentDialog.show(parentFragmentManager, "#bkash_payment_dialog")
        } else {
            viewModel.getBkashToken(amount)
        }
    }

    override fun onPaymentSuccess() {
        viewModel.hasBkashToken = false
        refreshUI()
    }

    override fun onPaymentError() {
        viewModel.hasBkashToken = false
    }

    override fun onPaymentCancelled() {
        viewModel.hasBkashToken = false
    }

    override fun onFosterPaymentSuccess() {
        viewModel.fosterUrl.postValue(Pair(null, null))
        refreshUI()
    }

    override fun onFosterPaymentError() {
        viewModel.fosterUrl.postValue(Pair(null, null))
    }

    override fun onFosterPaymentCancelled() {
        viewModel.fosterUrl.postValue(Pair(null, null))
    }
}