package ltd.royalgreen.pacecloud.servicemodule

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.service_fragment.*
import kotlinx.android.synthetic.main.service_vm_row.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.ServiceFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.RecyclerItemDivider
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ServiceFragment : Fragment(), Injectable {

    @Inject
    lateinit var api: ApiService

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
    private lateinit var adapter: DeploymentListAdapter

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

        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)

        adapter = DeploymentListAdapter(requireContext(), object : VMListAdapter.ActionCallback {
            override fun onNote() {

            }

            override fun onStop(success: Boolean) {
                if (success) {
//                    viewModel.deploymentList.value?.dataSource?.invalidate()
                } else {

                }
            }

            override fun onStart(success: Boolean) {
                if (success) {
//                    viewModel.deploymentList.value?.dataSource?.invalidate()
                } else {

                }
            }

            override fun onAttachDetach() {
                Toast.makeText(requireContext(), "Attach clicked from interface!", Toast.LENGTH_LONG).show()
            }

            override fun onReboot() {
                Toast.makeText(requireContext(), "Reboot clicked from interface!", Toast.LENGTH_LONG).show()
            }

            override fun onTerminate() {
                Toast.makeText(requireContext(), "Terminate clicked from interface!", Toast.LENGTH_LONG).show()
            }
        }, object : DeploymentListAdapter.RenameSuccessCallback {
            override fun onRenamed() {
                viewModel.deploymentList.value?.dataSource?.invalidate()
            }
        },requireActivity(), user?.resdata?.loggeduser)

        vmListRecycler.layoutManager = LinearLayoutManager(activity)
        vmListRecycler.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.deploymentList = viewModel.initializedPagedListBuilder(config).build()

        //3
        viewModel.deploymentList.observe(this, Observer<PagedList<Deployment>> { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.deploymentResponse.observe(this, Observer<Deployment> { value ->
            binding.tvm = value.totalNumberOfVMs.toString()
            binding.rvm = value.totalNumberOfRunningVMs.toString()
            binding.tNodeHour = BigDecimal(value.totalNodeHours?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
            binding.tCloudCost = BigDecimal(value.totalCloudCost?.toDouble()?:0.00).setScale(4, RoundingMode.HALF_UP).toString()
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
