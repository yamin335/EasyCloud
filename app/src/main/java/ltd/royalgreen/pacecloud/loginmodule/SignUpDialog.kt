package ltd.royalgreen.pacecloud.loginmodule

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.sign_up_dialog.view.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.SignUpDialogBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.util.autoCleared
import javax.inject.Inject

class SignUpDialog internal constructor(private val callBack: SignUpCallback) : DialogFragment(), View.OnClickListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginFragmentViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<SignUpDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.sign_up_dialog,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.passwordLayout.isEndIconVisible = false
        binding.confPasswordLayout.isEndIconVisible = false

        viewModel.firstName.observe(this, Observer {
            binding.signUp.isEnabled = !it.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.firstNameLayout.helperText = "Required"
            } else {
                binding.firstNameLayout.helperText = ""
            }
        })

        viewModel.lastName.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !it.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.lastNameLayout.helperText = "Required"
            } else {
                binding.lastNameLayout.helperText = ""
            }
        })

        viewModel.email.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !it.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.emailLayout.helperText = "Required"
            } else {
                binding.emailLayout.helperText = ""
                if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                    binding.emailLayout.isErrorEnabled = false
                    viewModel.isValidEmail.postValue(true)
                } else {
                    viewModel.isValidEmail.postValue(false)
                    binding.emailLayout.isErrorEnabled = true
                    binding.emailLayout.error = "Invalid Email!"
                }
            }
        })

        viewModel.mobile.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !it.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.mobileLayout.helperText = "Required"
            } else {
                binding.mobileLayout.helperText = ""
                if ("^(?=\\d)\\d{11}(?!\\d)".toRegex().matches(it)) {
                    binding.mobileLayout.isErrorEnabled = false
                    viewModel.isValidPhone.postValue(true)
                } else {
                    viewModel.isValidPhone.postValue(false)
                    binding.mobileLayout.isErrorEnabled = true
                    binding.mobileLayout.error = "Invalid Phone!"
                }
            }

        })

        viewModel.signUpPass.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !it.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.passwordLayout.helperText = "Required"
                binding.passwordLayout.isEndIconVisible = false
            } else {
                binding.passwordLayout.helperText = ""
                binding.passwordLayout.isEndIconVisible = true
            }
        })

        viewModel.signUpConfPass.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !it.isNullOrBlank() && viewModel.signUpPass.value == it &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            if (it == "") {
                binding.confPasswordLayout.helperText = "Required"
                binding.confPasswordLayout.isEndIconVisible = false
            } else {
                binding.confPasswordLayout.helperText = ""
                binding.confPasswordLayout.isEndIconVisible = true
                if (it == viewModel.signUpPass.value) {
                    binding.confPasswordLayout.isErrorEnabled = false
                } else {
                    binding.confPasswordLayout.isErrorEnabled = true
                    binding.confPasswordLayout.error = "Password doesn't match"
                }
            }
        })

        viewModel.isValidPhone.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && it == true
        })

        viewModel.isValidEmail.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    !viewModel.lastName.value.isNullOrBlank() && !viewModel.email.value.isNullOrBlank() &&
                    !viewModel.mobile.value.isNullOrBlank() && !viewModel.signUpPass.value.isNullOrBlank() &&
                    !viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    it == true && viewModel.isValidPhone.value == true
        })

        viewModel.company.observe(this, Observer {

        })

        view.signUp.setOnClickListener(this)
        view.cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signUp -> {
                val jsonObject = JsonObject().apply {
                    addProperty("cloudUserId", 0)
                    addProperty("firstName", viewModel.firstName.value)
                    addProperty("lastName", viewModel.lastName.value)
                    addProperty("password", viewModel.signUpPass.value)
                    addProperty("emailAddr", viewModel.email.value)
                    addProperty("companyName", viewModel.company.value)
//                    addProperty("phoneNumber", viewModel.mobile.value)
                    addProperty("phoneNumber", "")
                    addProperty("externalId", "")
                    addProperty("amount", 0)
                    addProperty("conpassword", viewModel.signUpConfPass.value)
                    addProperty("activationProfileId", 0)
                    addProperty("activationProfileName", "")
                    addProperty("deploystartVM", true)
                    add("username", null)
                    add("type", null)
                    add("accountSource", null)
                    add("VmPackage", JsonArray())

                }
                callBack.onSignUp(jsonObject)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface SignUpCallback{
        fun onSignUp(newUser: JsonObject)
    }
}