package ltd.royalgreen.pacecloud.loginmodule

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.login_container_activity.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.network.*
import javax.inject.Inject

const val SHARED_PREFS_KEY = "LoginStatus"

class LoginActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var exitDialog: NetworkStatusDialog

    private val viewModel: LoginViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = dispatchingAndroidInjector

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
                if (!exitDialog.isAdded )
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
