package ltd.royalgreen.pacecloud.supportmodule

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.SupportFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.network.ApiService
import ltd.royalgreen.pacecloud.util.autoCleared
import javax.inject.Inject

class SupportFragment : Fragment(), Injectable {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: SupportFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(SupportFragmentViewModel::class.java)
    }

    private var binding by autoCleared<SupportFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            fragmentManager?.let {
                exitDialog.show(it, "#app_exit_dialog")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.support_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
}
