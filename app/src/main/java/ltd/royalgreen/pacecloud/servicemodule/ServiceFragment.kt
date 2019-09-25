package ltd.royalgreen.pacecloud.servicemodule

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.service_fragment.*
import kotlinx.android.synthetic.main.service_vm_row.view.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.ServiceFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        if (isNetworkAvailable(requireContext())) {
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)

            adapter = DeploymentListAdapter(requireContext(), object : VMListAdapter.ActionCallback {
                override fun onNote() {

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

                override fun onReboot() {
//                Toast.makeText(requireContext(), "Reboot clicked from interface!", Toast.LENGTH_LONG).show()
                }

                override fun onTerminate() {
//                Toast.makeText(requireContext(), "Terminate clicked from interface!", Toast.LENGTH_LONG).show()
                }
            }, object : DeploymentListAdapter.RenameSuccessCallback {
                override fun onRenamed() {
                    viewModel.deploymentList.value?.dataSource?.invalidate()
                }
            }, parentFragmentManager, user?.resdata?.loggeduser)

            vmListRecycler.layoutManager = LinearLayoutManager(requireActivity())
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
        } else {
            val parent: ViewGroup? = null
            val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
            toastView.message.text = requireContext().getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }

        viewModel.deploymentResponse.observe(this, Observer<Deployment> { value ->
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

        viewModel.apiCallStatus.observe(this, Observer<ApiCallStatus> { status ->
            val parent: ViewGroup? = null
            when(status) {
                ApiCallStatus.SUCCESS -> {
                    Log.d("NOTHING", "Nothing to do")
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.virtual_machine_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.sync_and_refresh -> {
                syncDatabaseAndRefresh(requireContext())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun syncDatabaseAndRefresh(context: Context) {
        if (isNetworkAvailable(context)) {
            viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject()
            user?.let {
                jsonObject.apply {
                    addProperty("pageNumber", "1")
                    addProperty("pageSize", "20")
                    addProperty("id", it.resdata?.loggeduser?.userID?.toInt() ?: 0)
                }
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = api.clouduservmsyncwithlocaldb(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                            viewModel.deploymentList.value?.dataSource?.invalidate()
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
            val toast = Toast.makeText(context, "", Toast.LENGTH_LONG)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
            toastView.message.text = context.getString(R.string.net_error_msg)
            toast.view = toastView
            toast.show()
        }
    }

}
