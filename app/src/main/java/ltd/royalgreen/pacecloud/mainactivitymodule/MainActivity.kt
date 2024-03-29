package ltd.royalgreen.pacecloud.mainactivitymodule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_container.*
import kotlinx.android.synthetic.main.main_nav_header.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dashboardmodule.BalanceModel
import ltd.royalgreen.pacecloud.databinding.MainActivityBinding
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.loginmodule.LoginActivity
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainActivityViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var preferences: SharedPreferences

    lateinit var exitDialog: NetworkStatusDialog

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var appBarConfiguration: AppBarConfiguration


    var listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            "UserBalance" -> {
                val userBalance = Gson().fromJson(prefs.getString("UserBalance", null), BalanceModel::class.java)
                userBalance?.let {
                    nav_view.getHeaderView(0).loggedUserBalance.text = BigDecimal(it.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun androidInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        preferences.registerOnSharedPreferenceChangeListener(listener)
        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this, R.layout.main_activity
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.container.viewModel = viewModel
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        user?.let {
            nav_view.getHeaderView(0).loggedUserName.text = it.resdata?.loggeduser?.fullName
            nav_view.getHeaderView(0).loggedUserEmail.text = it.resdata?.loggeduser?.email
        }
        prepareSideNavMenu()
        viewModel.getUserBalance().observe(this, Observer { balance ->
            nav_view.getHeaderView(0).loggedUserBalance.text = BigDecimal(balance.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
        })

        exitDialog = NetworkStatusDialog(object : NetworkStatusDialog.NetworkChangeCallback {
            override fun onExit() {
                this@MainActivity.finish()
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

//        viewModel.apiCallStatus.observe(this, Observer {
//            when(it) {
//                "SUCCESS" -> Log.d("Successful", "Nothing to do")
//                "ERROR" -> {
//                    showErrorToast(this@MainActivity, this@MainActivity.getString(R.string.error_msg))
//                }
//                "NO_DATA" -> {
//                    showWarningToast(this@MainActivity, this@MainActivity.getString(R.string.no_data_msg))
//                }
//                "EMPTY" -> {
//                    showWarningToast(this@MainActivity, this@MainActivity.getString(R.string.empty_msg))
//                }
//                "TIMEOUT" -> {
//                    showWarningToast(this@MainActivity, this@MainActivity.getString(R.string.timeout_msg))
//                }
//                else -> Log.d("NOTHING", "Nothing to do")
//            }
//        })

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

//        bottom_nav.setOnNavigationItemSelectedListener {
//            it.isCheckable = true
//            it.isChecked = true
//            false
//        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.dashboard_graph,
            R.navigation.service_graph,
            R.navigation.payment_graph,
            R.navigation.support_graph
        )

        // Setup the bottom navigation view with a payment_graph of navigation graphs
        val controller = bottom_nav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            appBarConfiguration = AppBarConfiguration(
                navGraph = navController.graph,
                drawerLayout = drawer_layout
            )
            // Set up ActionBar
            setSupportActionBar(toolbar)
            setupActionBarWithNavController(navController, appBarConfiguration)
            nav_view.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.dashboardScreen -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.serviceScreen -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.paymentScreen -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    R.id.supportScreen -> {
                        bottom_nav.visibility = View.VISIBLE
                    }
                    else -> bottom_nav.visibility = View.GONE
                }
            }

//            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp(appBarConfiguration) ?: false || super.onSupportNavigateUp()
    }

    private fun doSignOut() {
        val signOutDialog = CustomAlertDialog(object :  CustomAlertDialog.YesCallback{
            override fun onYes() {
                val handler = CoroutineExceptionHandler { _, exception ->
                    exception.printStackTrace()
                }
                CoroutineScope(Dispatchers.IO).launch(handler) {
                    preferences.edit().apply {
                        putString("LoggedUser", "")
                        apply()
                    }
                }
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }, "Do you want to sign out?", "")
        signOutDialog.show(supportFragmentManager, "#app_signout_dialog")
    }

    private fun prepareSideNavMenu() {
        val menuHeaderList = ArrayList<ExpandableMenuModel>()
        val menuChildMap = HashMap<ExpandableMenuModel, List<String>>()

        val aboutHeader = ExpandableMenuModel("About Us", R.drawable.ic_info_white_24dp)
        menuHeaderList.add(aboutHeader)

        val signoutHeader = ExpandableMenuModel("Sign Out",
            R.drawable.ic_exit_to_app_white_24dp
        )
        menuHeaderList.add(signoutHeader)

        val aboutChild = ArrayList<String>()
        val signoutChild = ArrayList<String>()

        menuChildMap[menuHeaderList[0]] = aboutChild
        menuChildMap[menuHeaderList[1]] = signoutChild

        val sideMenuAdapter = ExpandableMenuAdapter(this, menuHeaderList, menuChildMap)
        expandableMenu.setAdapter(sideMenuAdapter)

        expandableMenu.setOnGroupClickListener { _, _, i, l ->
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            when(currentNavController?.value?.graph?.id) {
                R.id.dashboard_graph -> {
                    if (i == 0 && l == 0L) {
                        currentNavController?.value?.navigate(R.id.action_dashboardScreen_to_about_graph)
                    }
                }
                R.id.service_graph -> {
                    if (i == 0 && l == 0L) {
                        currentNavController?.value?.navigate(R.id.action_serviceScreen_to_about_graph)
                    }
                }
                R.id.payment_graph -> {
                    if (i == 0 && l == 0L) {
                        currentNavController?.value?.navigate(R.id.action_paymentScreen_to_aboutFragment)
                    }
                }
                R.id.support_graph -> {
                    if (i == 0 && l == 0L) {
                        currentNavController?.value?.navigate(R.id.action_supportScreen_to_about_graph)
                    }
                }
            }

            if (i == 1 && l == 1L) {
                doSignOut()
            }
            return@setOnGroupClickListener true
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}