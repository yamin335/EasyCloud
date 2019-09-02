package ltd.royalgreen.pacecloud.mainactivitymodule

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
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
import ltd.royalgreen.pacecloud.util.setupWithNavController
import ltd.royalgreen.pacecloud.util.ExpandableMenuAdapter
import ltd.royalgreen.pacecloud.util.ExpandableMenuModel
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainActivityViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var preferences: SharedPreferences

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var reCreate: MutableLiveData<Boolean>

    var listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key) {
                "UserBalance" -> {
                    val userBalance = Gson().fromJson(prefs.getString("UserBalance", null), BalanceModel::class.java)
                    userBalance?.let {
                        viewModel.userBalance.value = it
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

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        reCreate = MutableLiveData<Boolean>()
        reCreate.value = false
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
        viewModel.userBalance.observe(this, Observer { balance ->
            nav_view.getHeaderView(0).loggedUserBalance.text = BigDecimal(balance.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
        })

        reCreate.observe(this, Observer { reCreate ->
           if (reCreate) {
               Toast.makeText(this@MainActivity, "Successfully Synced Data!", Toast.LENGTH_LONG).show()
               this@MainActivity.recreate()
           }
        })

        viewModel.apiCallStatus.observe(this, Observer {
            when(it) {
                ApiCallStatus.SUCCESS -> Log.d("Successful", "Nothing to do")
                ApiCallStatus.ERROR -> Toast.makeText(this, "Can not connect to SERVER!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.TIMEOUT -> Toast.makeText(this, "SERVER is not responding!!!", Toast.LENGTH_LONG).show()
                ApiCallStatus.EMPTY -> Toast.makeText(this, "Empty return value!!!", Toast.LENGTH_LONG).show()
                else -> Log.d("NOTHING", "Nothing to do")
            }
        })

        if (savedInstanceState == null) {
            viewModel.getUserBalance(user)
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
            appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        currentNavController?.observe(this, Observer { navController ->
            when(navController.graph.id) {
                R.id.dashboard_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
                R.id.service_graph -> menuInflater.inflate(R.menu.virtual_machine_menu, menu)
                R.id.payment_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
                R.id.support_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.refresh -> recreate()
//            R.id.sync_and_refresh -> recreate()
            R.id.sync_and_refresh -> syncDatabaseAndRefresh(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp(appBarConfiguration) ?: false || super.onSupportNavigateUp()
    }

    private fun doSignOut() {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle("Do you want sign out?")
            .setIcon(R.mipmap.app_logo_new)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
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
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        exitDialog.show()
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
                        currentNavController?.value?.navigate(R.id.action_paymentScreen_to_about_graph)
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

        expandableMenu.setOnChildClickListener { expandableListView, view, headerID, i, l ->
            return@setOnChildClickListener false
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun syncDatabaseAndRefresh(activity: Activity) {
        if (isNetworkAvailable(activity)) {
            viewModel.apiCallStatus.postValue(ApiCallStatus.LOADING)
            val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
            val jsonObject = JsonObject()
            user?.let {
                jsonObject.apply {
                    addProperty("pageNumber", "1")
                    addProperty("pageSize", "20")
                    addProperty("id", it.resdata?.loggeduser?.userID?.toInt() ?: 0)
                }
            }

            val handler = CoroutineExceptionHandler { _, exception ->
                viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                exception.printStackTrace()
            }

            val param = JsonArray().apply {
                add(jsonObject)
            }

            CoroutineScope(Dispatchers.IO).launch(handler) {
                val response = apiService.clouduservmsyncwithlocaldb(param).execute()
                when (val apiResponse = ApiResponse.create(response)) {
                    is ApiSuccessResponse -> {
                        if (JsonParser().parse(apiResponse.body).asJsonObject.getAsJsonObject("resdata").get("resstate").asBoolean) {
                            reCreate.postValue(true)
                            viewModel.apiCallStatus.postValue(ApiCallStatus.SUCCESS)
                        }
                    }
                    is ApiEmptyResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.EMPTY)
                    }
                    is ApiErrorResponse -> {
                        viewModel.apiCallStatus.postValue(ApiCallStatus.ERROR)
                    }
                }
            }
        } else {
            Toast.makeText(activity, "Please check Your internet connection!", Toast.LENGTH_LONG).show()
        }
    }
}