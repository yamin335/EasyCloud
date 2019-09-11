package ltd.royalgreen.pacecloud.loginmodule

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.login_container_activity.*
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivity
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.databinding.LoginFragmentBinding
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

const val SHARED_PREFS_KEY = "LoginStatus"

class LoginActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var exitDialog: NetworkStatusDialog

    private val viewModel: LoginViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_container_activity)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(findNavController(R.id.fragment))

        exitDialog = NetworkStatusDialog(object : NetworkStatusDialog.NetworkChangeCallback {
            override fun onExit() {
                this@LoginActivity.finish()
            }
        })
        exitDialog.isCancelable = false

        viewModel.internetStatus.observe(this, Observer {
            if (it) {
                if (exitDialog.isVisible)
                    exitDialog.dismiss()
            } else {
                exitDialog.show(supportFragmentManager, "#net_status_dialog")
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }
}
