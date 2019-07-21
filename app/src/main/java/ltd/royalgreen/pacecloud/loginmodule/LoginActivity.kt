package ltd.royalgreen.pacecloud.loginmodule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.MainActivity
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.databinding.LoginActivityBinding
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

const val SHARED_PREFS_KEY = "LoginStatus"

class LoginActivity : AppCompatActivity(){

    @Inject
    lateinit var preferences: SharedPreferences

    private val viewModel: LoginViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
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

        loginButton.setOnClickListener {
            viewModel.errorMessage.value = false
            usernametInputLayout.isErrorEnabled = false
            passwordInputLayout.isErrorEnabled = false
            when {
                viewModel.userName.value.isNullOrEmpty() -> {
                    passwordInputLayout.isErrorEnabled = false
                    usernametInputLayout.isErrorEnabled = true
                    usernametInputLayout.error = "Username required!"
                    usernametInputLayout.requestFocus()
                }
                viewModel.password.value.isNullOrEmpty() -> {
                    usernametInputLayout.isErrorEnabled = false
                    passwordInputLayout.isErrorEnabled = true
                    passwordInputLayout.error = "Password required!"
                    passwordInputLayout.requestFocus()
                }
                else -> viewModel.processSignIn()
            }
        }

        viewModel.apiCallStatus.observe(this, Observer {
            when(it) {
                ApiCallStatus.SUCCESS -> {
                    Toast.makeText(this, "Signed in successfully", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                ApiCallStatus.ERROR -> Toast.makeText(this, "Unexpected ERROR occured!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.TIMEOUT -> Toast.makeText(this, "SERVER is not responding...", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this, "$exception", Toast.LENGTH_LONG).show()
                    }
                    CoroutineScope(Dispatchers.IO).launch(handler) {
                        val loggedUserSerialized = Gson().toJson(loggedUser)
                        preferences.edit().apply {
                            putBoolean("LoginState", true)
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
            .setIcon(R.mipmap.ic_launcher)
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
