package ltd.royalgreen.pacecloud.servicemodule

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.service_fragment.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.ServiceFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import ltd.royalgreen.pacecloud.util.autoCleared
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ServiceFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: ServiceFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(ServiceFragmentViewModel::class.java)
    }

    private var binding by autoCleared<ServiceFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Cloud User Activity Log
    private lateinit var adapter: VMListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.service_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        adapter = VMListAdapter(this.context!!)

        vmListRecycler.layoutManager = LinearLayoutManager(activity)
        vmListRecycler.addItemDecoration(RecyclerItemDivider(activity!!.applicationContext, LinearLayoutManager.VERTICAL, 8))
        vmListRecycler.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.vmList = viewModel.initializedPagedListBuilder(config).build()

        //3
        viewModel.vmList.observe(this, Observer<PagedList<VM>> { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.vmResponse.observe(this, Observer<VMListResponse> { value ->
            binding.tvm = value.resdata?.totalNumberOfVMs.toString()
            binding.rvm = value.resdata?.totalNumberOfRunningVMs.toString()
            binding.tNodeHour = BigDecimal(value.resdata?.totalNodeHours?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
            binding.tCloudCost = BigDecimal(value.resdata?.totalCloudCost?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
        })

        viewModel.apiCallStatus.observe(this, Observer<ApiCallStatus> { status ->
            when(status) {
                ApiCallStatus.LOADING -> {
                    binding.loader.visibility = View.VISIBLE
                }
                else -> binding.loader.visibility = View.GONE
            }
        })
    }
}
