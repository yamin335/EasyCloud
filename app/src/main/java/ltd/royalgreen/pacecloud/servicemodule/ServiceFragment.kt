package ltd.royalgreen.pacecloud.servicemodule

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.service_fragment.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.ServiceFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.showErrorToast
import ltd.royalgreen.pacecloud.util.showWarningToast
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ServiceFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

//    val viewModel: ServiceFragmentViewModel by viewModels()

    val viewModelReference by viewModels<ServiceFragmentViewModel>()

    val viewModel: ServiceFragmentViewModel by viewModels {
        viewModelFactory
    }

    private var binding by autoCleared<ServiceFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    //For Cloud User Activity Log
    private lateinit var adapter: DeploymentListAdapter

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
            R.layout.service_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lifecycleOwner = null
        //binding.viewModel = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        //binding.viewModel = viewModel



        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)

        adapter = DeploymentListAdapter(object : VMListAdapter.ActionCallback {
            override fun onNote(success: Boolean) {
                if (success) {
                    viewModel.deploymentList.value?.dataSource?.invalidate()
                }
            }

            override fun onStop(success: Boolean) {
//                    if (success) {
////                    viewModel.deploymentList.value?.dataSource?.invalidate()
//                    } else {
//
//                    }
            }

            override fun onStart(success: Boolean) {
//                    if (success) {
////                    viewModel.deploymentList.value?.dataSource?.invalidate()
//                    } else {
//
//                    }
            }

            override fun onAttachDetach() {
//                Toast.makeText(requireContext(), "Attach clicked from interface!", Toast.LENGTH_LONG).show()
            }

            override fun onReboot(success: Boolean) {
//                Toast.makeText(requireContext(), "Reboot clicked from interface!", Toast.LENGTH_LONG).show()
            }

            override fun onTerminate() {
//                Toast.makeText(requireContext(), "Terminate clicked from interface!", Toast.LENGTH_LONG).show()
            }
        }, object : DeploymentListAdapter.RenameSuccessCallback {
            override fun onRenamed() {
                viewModel.deploymentList.value?.dataSource?.invalidate()
            }
        }, parentFragmentManager, user.resdata?.loggeduser?.fullName, viewModel, this)

        vmListRecycler.layoutManager = LinearLayoutManager(requireActivity())
        vmListRecycler.adapter = adapter

        //1
        val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()

        //2
        viewModel.deploymentList = initializedPagedListBuilder(config).build()

        //3
        viewModel.deploymentList.observe(this, Observer<PagedList<Deployment>> { pagedList ->
            adapter.submitList(pagedList)
        })

        viewModel.runningVmStatus.observe(this, Observer<Deployment> { value ->
            value?.let {
                binding.noVM.visibility = View.GONE
                binding.tvm = value.totalNumberOfVMs.toString()
                binding.rvm = value.totalNumberOfRunningVMs.toString()
                binding.tNodeHour = BigDecimal(value.totalNodeHours?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
                binding.tCloudCost = BigDecimal(value.totalCloudCost?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
            } ?: run {
                binding.tvm = "0"
                binding.rvm = "0"
                binding.noVM.visibility = View.VISIBLE
            }
        })

        viewModel.apiCallStatus.observe(this, Observer<String> { status ->
            when(status) {
                "SUCCESS" -> {
                    Log.d("NOTHING", "Nothing to do")
                }
                else -> Log.d("ELSE", "Else to do")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.virtual_machine_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.sync_and_refresh -> {
                viewModel.refreshDatabaseAndUI.observe(this, Observer {
                    if (it) {
                        viewModel.deploymentList.value?.dataSource?.invalidate()
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initializedPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Long, Deployment> {
        val dataSourceFactory = object : DataSource.Factory<Long, Deployment>() {
            override fun create(): DataSource<Long, Deployment> {
                return DeploymentListDataSource(viewModelReference, preferences)
            }
        }
        return LivePagedListBuilder<Long, Deployment>(dataSourceFactory, config)
    }
}
