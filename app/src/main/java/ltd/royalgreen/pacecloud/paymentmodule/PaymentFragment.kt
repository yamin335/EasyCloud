package ltd.royalgreen.pacecloud.paymentmodule

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.payment_bottom_sheet.*
import kotlinx.android.synthetic.main.payment_bottom_sheet.view.*
import kotlinx.android.synthetic.main.payment_content_main.*
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.android.synthetic.main.service_fragment.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.PaymentFragmentBinding
import ltd.royalgreen.pacecloud.databinding.ServiceFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.servicemodule.VM
import ltd.royalgreen.pacecloud.servicemodule.VMListAdapter
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class PaymentFragment : Fragment(), Injectable, PaymentRechargeDialog.RechargeCallback {

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
            showRechargeDialog()
        }

        binding.includedBottomSheet.applyFilter.setOnClickListener {
            applySearch()
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.includedBottomSheet.viewModel = viewModel
        binding.includedContentMain.viewModel = viewModel

        adapter = PaymentListAdapter()

        binding.includedContentMain.paymentRecycler.layoutManager = LinearLayoutManager(activity)
        binding.includedContentMain.paymentRecycler.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        binding.includedContentMain.paymentRecycler.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.paymentList = viewModel.initializedPagedListBuilder(config).build()

        //3
        viewModel.paymentList.observe(this, Observer<PagedList<BilCloudUserLedger>> { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.apiCallStatus.observe(this, Observer<ApiCallStatus> { status ->
            when(status) {
                ApiCallStatus.LOADING -> {
                    binding.includedContentMain.loader.visibility = View.VISIBLE
                }
                else -> binding.includedContentMain.loader.visibility = View.GONE
            }
        })

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            viewModel.getUserBalance(it)
        }
    }

    private fun applySearch() {
        viewModel.paymentList.value?.dataSource?.invalidate()
        if (bottomSheeetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheeetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            searchFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp, activity!!.theme))
        }
    }

    override fun onSavePressed(date: String, amount: String, note: String) {
        val result = date+"~~~~~~"+amount+"~~~~~~"+note
//        if (isNetworkAvailable(activity!!)) {
//            viewModel.apiCallStatus.value = ApiCallStatus.LOADING
//            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
//            user?.let {
//                viewModel.getUserBalance(it)
//            }
//            val jsonObject = JsonObject().apply {
//                addProperty("UserID", user?.resdata?.loggeduser?.userID)
//            }
//            val param = JsonArray().apply {
//                add(jsonObject)
//            }.toString()
//
//            val handler = CoroutineExceptionHandler { _, exception ->
//                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
//            }
//
//            CoroutineScope(Dispatchers.IO).launch(handler) {
//                withTimeoutOrNull(3000L) {
//                    val response = apiService.billclouduserbalance(param).execute()
//                    val apiResponse = ApiResponse.create(response)
//                    when (apiResponse) {
//                        is ApiSuccessResponse -> {
//                            val balanceModel = apiResponse.body
//                            userBalance.postValue(BigDecimal(balanceModel.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString())
//                            apiCallStatus.value = ApiCallStatus.SUCCESS
//                        }
//                        is ApiEmptyResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.EMPTY)
//                        }
//                        is ApiErrorResponse -> {
//                            apiCallStatus.postValue(ApiCallStatus.ERROR)
//                        }
//                    }
//                }
//            }
//        } else {
//            Toast.makeText(application, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
//        }
    }

    private fun showRechargeDialog() {
        val rechargeDialog = PaymentRechargeDialog(requireActivity(), this)
        rechargeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rechargeDialog.setCancelable(true)
        rechargeDialog.show()
    }
}

//class MyAdapter(private val myDataset: Array<String>) :
//    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder.
//    // Each data item is just a string in this case that is shown in a TextView.
//    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)
//
//
//    // Create new views (invoked by the layout manager)
//    override fun onCreateViewHolder(parent: ViewGroup,
//                                    viewType: Int): ViewHolder {
//        // create a new view
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.list_view_item, parent, false)
//
//
//        return ViewHolder(itemView)
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]
//
//        holder.item.findViewById<ImageView>(R.id.user_avatar_image)
//                .setImageResource(listOfAvatars[position % listOfAvatars.size])
//
//        holder.item.setOnClickListener {
//            val bundle = bundleOf(USERNAME_KEY to myDataset[position])
//
//            holder.item.findNavController().navigate(
//                    R.id.action_leaderboard_to_userProfile,
//                bundle)
//        }
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount() = myDataset.size
//
//    companion object {
//        const val USERNAME_KEY = "userName"
//    }
//}
//
//private val listOfAvatars = listOf(
//    R.drawable.avatar_1_raster,
//    R.drawable.avatar_2_raster,
//    R.drawable.avatar_3_raster,
//    R.drawable.avatar_4_raster,
//    R.drawable.avatar_5_raster,
//    R.drawable.avatar_6_raster
//)
