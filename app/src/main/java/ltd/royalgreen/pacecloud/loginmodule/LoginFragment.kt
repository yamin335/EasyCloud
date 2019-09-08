package ltd.royalgreen.pacecloud.loginmodule


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.LoginFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivity
import ltd.royalgreen.pacecloud.network.ApiCallStatus
import ltd.royalgreen.pacecloud.network.ApiService
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: LoginFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(LoginFragmentViewModel::class.java)
    }

    private var binding by autoCleared<LoginFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Do you want to exit?")
                .setIcon(R.mipmap.app_logo_new)
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    preferences.edit().apply {
                        putString("LoggedUser", "")
                        apply()
                    }
                    requireActivity().finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            exitDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.login_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.passwordInputLayout.isEndIconVisible = false

        binding.loginButton.setOnClickListener {
            viewModel.errorMessage.value = false
            viewModel.processSignIn()
        }

        binding.forgotPassword.setOnClickListener {

        }

        binding.signUp.setOnClickListener {
            val signUpDialog = SignUpDialog(object : SignUpDialog.SignUpCallback {
                override fun onSignUp(newUser: JsonObject) {
                    Toast.makeText(context, "Creating Account Please Wait...", Toast.LENGTH_LONG).show()
                    viewModel.processSignUp(newUser)
                }
            })
//            signUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            val params = signUpDialog.window?.attributes
//            signUpDialog.window?.attributes = WindowManager.LayoutParams()
            signUpDialog.isCancelable = true
            signUpDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen)
            fragmentManager?.let {
                signUpDialog.show(it, "#sign_up_dialog")
            }
        }

        binding.faq.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_faqsFragment)
        }

        binding.privacy.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_privacyFragment)
        }

        binding.contact.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_contactFragment)
        }

        viewModel.userName.observe(this, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.password.value.isNullOrEmpty()

        })

        viewModel.password.observe(this, Observer {
            viewModel.errorMessage.value = false
            binding.loginButton.isEnabled = !it.isNullOrEmpty() && !viewModel.userName.value.isNullOrEmpty()
            binding.passwordInputLayout.isEndIconVisible = !it.isNullOrEmpty()
        })

        viewModel.apiCallStatus.observe(this, Observer {
            when(it) {
                ApiCallStatus.SUCCESS -> Log.d("SUCCESSFUL", "Nothing to do")
                ApiCallStatus.ERROR -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                    toastView.message.text = requireContext().getString(R.string.error_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.NO_DATA -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                    toastView.message.text = requireContext().getString(R.string.no_data_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.EMPTY -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                    toastView.message.text = requireContext().getString(R.string.empty_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.TIMEOUT -> {
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                    toastView.message.text = requireContext().getString(R.string.timeout_msg)
                    toast.view = toastView
                    toast.show()
                }
                ApiCallStatus.INVALID_USERNAME -> {
                    viewModel.errorMessage.value = true
                }
                ApiCallStatus.INVALID_PASSWORD -> {
                    viewModel.errorMessage.value = true
                }
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })

        viewModel.signUpMsg.observe(this, Observer {
            if (it == "Save successfully.") {
                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = inflater.inflate(R.layout.toast_custom_green, null)
                toastView.message.text = requireContext().getString(R.string.acc_successful)
                toast.view = toastView
                toast.show()
            } else {
                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = inflater.inflate(R.layout.toast_custom_red, null)
                toastView.message.text = it
                toast.view = toastView
                toast.show()
            }
        })

        viewModel.apiResult.observe(this, Observer { loggedUser ->
            when (loggedUser?.resdata?.message) {
                "Username does not exist." -> viewModel.apiCallStatus.value = ApiCallStatus.INVALID_USERNAME
                "Password is wrong." -> viewModel.apiCallStatus.value = ApiCallStatus.INVALID_PASSWORD
                "User not active." -> {
                    viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                    val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val toastView = inflater.inflate(R.layout.toast_custom_green, null)
                    toastView.message.text = requireContext().getString(R.string.user_not_active)
                    toast.view = toastView
                    toast.show()
                }
                else -> {
                    if (loggedUser?.resdata?.resstate == true) {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        val handler = CoroutineExceptionHandler { _, exception ->
                            exception.printStackTrace()
                        }
                        CoroutineScope(Dispatchers.IO).launch(handler) {
                            val loggedUserSerialized = Gson().toJson(loggedUser)
                            preferences.edit().apply {
                                //                            putBoolean("LoginState", true)
                                putString("LoggedUser", loggedUserSerialized)
                                apply()
                            }
                        }
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.NO_DATA)
                    }
                }
            }
        })
    }
}
