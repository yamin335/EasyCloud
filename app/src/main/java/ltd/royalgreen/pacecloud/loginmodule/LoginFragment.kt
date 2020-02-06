package ltd.royalgreen.pacecloud.loginmodule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.LoginFragmentBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.mainactivitymodule.CustomAlertDialog
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivity
import ltd.royalgreen.pacecloud.util.*
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: LoginFragmentViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<LoginFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            val exitDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
                override fun onYes() {
                    viewModel.onAppExit(preferences)
                    requireActivity().finish()
                }
            }, "Do you want to exit?", "")
            parentFragmentManager.let {
                exitDialog.show(it, "#app_exit_dialog")
            }
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

        binding.username.setOnFocusChangeListener { _, _ ->
            viewModel.errorMessage.value = false
        }

        binding.password.setOnFocusChangeListener { _, _ ->
            viewModel.errorMessage.value = false
        }

        binding.loginButton.setOnClickListener {
            hideKeyboard()
            signIn()
        }

        binding.forgotPassword.setOnClickListener {

        }

        binding.signUp.setOnClickListener {
            val signUpDialog = SignUpDialog(object : SignUpDialog.SignUpCallback {
                override fun onSignUp(newUser: JsonObject) {
                    showErrorToast(requireContext(), requireContext().getString(R.string.loading_msg))
                    signUp(newUser)
                }
            })
            signUpDialog.isCancelable = true
            signUpDialog.show(parentFragmentManager, "#sign_up_dialog")
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
                "SUCCESS" -> Log.d("SUCCESSFUL", "Nothing to do")
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
                "INVALID_USERNAME" -> {
                    viewModel.errorMessage.value = true
                }
                "INVALID_PASSWORD" -> {
                    viewModel.errorMessage.value = true
                }
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })
    }

    private fun signIn() {
        viewModel.doSignIn().observe(this, Observer { loggedUser ->
            when (loggedUser?.resdata?.message) {
                "Username does not exist." -> viewModel.apiCallStatus.postValue("INVALID_USERNAME")
                "Password is wrong." -> viewModel.apiCallStatus.postValue("INVALID_PASSWORD")
                "User not active." -> {
                    viewModel.apiCallStatus.postValue("SUCCESS")
                    showWarningToast(requireContext(), requireContext().getString(R.string.user_not_active))
                }
                else -> {
                    if (loggedUser?.resdata?.loggeduser != null) {
                        viewModel.apiCallStatus.postValue("SUCCESS")
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
                        viewModel.apiCallStatus.postValue("NO_DATA")
                    }
                }
            }
        })
    }

    private fun signUp(jsonObject: JsonObject) {
        viewModel.doSignUp(jsonObject).observe(this, Observer {
            if (it == "Save successfully.") {
                showSuccessToast(requireContext(), requireContext().getString(R.string.acc_successful))
            } else {
                showErrorToast(requireContext(), it)
            }
        })
    }
}
