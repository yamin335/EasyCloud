package ltd.royalgreen.pacecloud.loginmodule

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivity
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.databinding.LoginActivityBinding
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

const val SHARED_PREFS_KEY = "LoginStatus"

class LoginActivity : AppCompatActivity(){

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    public override fun onResume() {
        super.onResume()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val binding: LoginActivityBinding = DataBindingUtil.setContentView(
            this, R.layout.login_activity)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.passwordInputLayout.isEndIconVisible = false

        loginButton.setOnClickListener {
            viewModel.errorMessage.value = false
            viewModel.processSignIn()
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
                ApiCallStatus.SUCCESS -> {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                ApiCallStatus.ERROR -> Toast.makeText(this, "Can not connect to SERVER!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.TIMEOUT -> Toast.makeText(this, "SERVER is not responding!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.EMPTY -> Toast.makeText(this, "Empty return value!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.INVALIDUSERNAME -> {
                    viewModel.errorMessage.value = true
                }
                ApiCallStatus.INVALIDPASSWORD -> {
                    viewModel.errorMessage.value = true
                }
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })

        viewModel.apiResult.observe(this, Observer { loggedUser ->
            when (loggedUser?.resdata?.message) {
                "Username does not exist." -> viewModel.apiCallStatus.value = ApiCallStatus.INVALIDUSERNAME
                "Password is wrong." -> viewModel.apiCallStatus.value = ApiCallStatus.INVALIDPASSWORD
                else -> {
                    viewModel.apiCallStatus.value = ApiCallStatus.SUCCESS
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
                }
            }
        })
    }

    override fun onBackPressed() {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle("Do you want to exit?")
            .setIcon(R.mipmap.app_logo_new)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        exitDialog.show()
    }
}
