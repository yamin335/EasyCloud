package ltd.royalgreen.pacecloud.loginmodule

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.PatternMatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
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
import java.util.regex.Pattern
import javax.inject.Inject

class SignUpDialog internal constructor(private val callBack: SignUpCallback) : DialogFragment(), View.OnClickListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(LoginFragmentViewModel::class.java)
    }

    private var binding by autoCleared<SignUpDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
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
            if (it == "") {
                binding.firstNameLayout.helperText = "Required"
            } else {
                binding.firstNameLayout.helperText = ""
                binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                        viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                        viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                        viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                        viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            }
        })

        viewModel.lastName.observe(this, Observer {
            if (it == "") {
                binding.lastNameLayout.helperText = "Required"
            } else {
                binding.lastNameLayout.helperText = ""
                binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                        viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                        viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                        viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                        viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            }
        })

        viewModel.email.observe(this, Observer {
            if (it == "") {
                binding.emailLayout.helperText = "Required"
            } else {
                binding.emailLayout.helperText = ""
                if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                    binding.emailLayout.isErrorEnabled = false
                    viewModel.isValidEmail.postValue(true)
                    binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                            viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                            viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                            viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                            viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
                } else {
                    viewModel.isValidEmail.postValue(false)
                    binding.emailLayout.isErrorEnabled = true
                    binding.emailLayout.error = "Invalid Email!"
                }
            }
        })

        viewModel.mobile.observe(this, Observer {
            if (it == "") {
                binding.mobileLayout.helperText = "Required"
            } else {
                binding.mobileLayout.helperText = ""
                if ("^(?=\\d)\\d{11}(?!\\d)".toRegex().matches(it)) {
                    binding.mobileLayout.isErrorEnabled = false
                    viewModel.isValidPhone.postValue(true)
                    binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                            viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                            viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                            viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                            viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
                } else {
                    viewModel.isValidPhone.postValue(false)
                    binding.mobileLayout.isErrorEnabled = true
                    binding.mobileLayout.error = "Invalid Phone!"
                }
            }

//            val a = viewModel.firstName.value != ""
//            val b = viewModel.lastName.value != ""
//            val c = viewModel.email.value != ""
//            val d = viewModel.mobile.value != ""
//            val e = viewModel.signUpPass.value != ""
//            val test1 = viewModel.signUpPass.value
//            val f = viewModel.signUpConfPass.value != ""
//            val test2 = viewModel.signUpConfPass.value
//            val g = viewModel.signUpPass.value == viewModel.signUpConfPass.value
//            val h = viewModel.isValidEmail.value == true
//            val i = viewModel.isValidPhone.value == true
//            val j = viewModel.firstName.value != "" && viewModel.lastName.value != "" &&
//                    viewModel.email.value != "" && viewModel.mobile.value != "" &&
//                    viewModel.signUpPass.value != "" && viewModel.signUpConfPass.value != "" &&
//                    viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
//                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
//            val k = j
        })

        viewModel.signUpPass.observe(this, Observer {
            if (it == "") {
                binding.passwordLayout.helperText = "Required"
                binding.passwordLayout.isEndIconVisible = false
            } else {
                binding.passwordLayout.helperText = ""
                binding.passwordLayout.isEndIconVisible = true
                binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                        viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                        viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                        viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                        viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
            }
        })

        viewModel.signUpConfPass.observe(this, Observer {
            if (it == "") {
                binding.confPasswordLayout.helperText = "Required"
                binding.confPasswordLayout.isEndIconVisible = false
            } else {
                binding.confPasswordLayout.helperText = ""
                binding.confPasswordLayout.isEndIconVisible = true
                if (it == viewModel.signUpPass.value) {
                    binding.confPasswordLayout.isErrorEnabled = false
                    binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                            viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                            viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                            viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                            viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
                } else {
                    binding.confPasswordLayout.isErrorEnabled = true
                    binding.confPasswordLayout.error = "Password doesn't match"
                }
            }
        })

        viewModel.isValidPhone.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                    viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                    viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
        })

        viewModel.isValidEmail.observe(this, Observer {
            binding.signUp.isEnabled = !viewModel.firstName.value.isNullOrBlank() &&
                    viewModel.lastName.value.isNullOrBlank() && viewModel.email.value.isNullOrBlank() &&
                    viewModel.mobile.value.isNullOrBlank() && viewModel.signUpPass.value.isNullOrBlank() &&
                    viewModel.signUpConfPass.value.isNullOrBlank() && viewModel.signUpPass.value == viewModel.signUpConfPass.value &&
                    viewModel.isValidEmail.value == true && viewModel.isValidPhone.value == true
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