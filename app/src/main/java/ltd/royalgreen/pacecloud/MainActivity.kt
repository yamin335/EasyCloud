package ltd.royalgreen.pacecloud

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.nav_drawer.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.databinding.MainActivityBinding
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.loginmodule.LoginActivity
import ltd.royalgreen.pacecloud.util.ExpandableMenuAdapter
import ltd.royalgreen.pacecloud.util.ExpandableMenuModel
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {
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

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this, R.layout.main_activity)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        if (user != null) {
            nav_view.getHeaderView(0).loggedUserName.text = user.resdata?.loggeduser?.fullName
            nav_view.getHeaderView(0).loggedUserEmail.text = user.resdata?.loggeduser?.email
        }
        prepareSideNavMenu()
        viewModel.userBalance.observe(this, Observer { balance ->
            nav_view.getHeaderView(0).loggedUserBalance.text = BigDecimal(balance.resdata?.billCloudUserBalance?.balanceAmount?.toDouble()?:0.00).setScale(2, RoundingMode.HALF_UP).toString()
        })

        if (savedInstanceState == null) {
            viewModel.getUserBalance(user)
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
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
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(R.navigation.dashboard_graph, R.navigation.service_graph, R.navigation.payment_graph, R.navigation.support_graph)

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
//            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        currentNavController?.observe(this, Observer { navController ->
            when(navController.graph.id) {
                R.id.dashboard_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
                R.id.service_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
                R.id.payment_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
                R.id.support_graph -> menuInflater.inflate(R.menu.dashboard_menu, menu)
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.signOut -> doSignOut()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp(appBarConfiguration) ?: false || super.onSupportNavigateUp()
    }

    private fun doSignOut() {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
            .setTitle("Do you want sign out?")
            .setIcon(R.mipmap.ic_launcher)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val handler = CoroutineExceptionHandler { _, exception ->
                    Toast.makeText(this, "$exception", Toast.LENGTH_LONG).show()
                }
                CoroutineScope(Dispatchers.IO).launch(handler) {
                    preferences.edit().apply {
                        putBoolean("LoginState", false)
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

        val demoMenuHeader = ExpandableMenuModel("Menu Header", R.drawable.ic_star_white_24dp)
        menuHeaderList.add(demoMenuHeader)

        val aboutHeader = ExpandableMenuModel("About Us", R.drawable.ic_info_white_24dp)
        menuHeaderList.add(aboutHeader)

        val demoMenuChild = ArrayList<String>()
        demoMenuChild.add("First Demo Child")
        demoMenuChild.add("Second Demo Child")

        val aboutChild = ArrayList<String>()

        menuChildMap[menuHeaderList[0]] = demoMenuChild
        menuChildMap[menuHeaderList[1]] = aboutChild

        val sideMenuAdapter = ExpandableMenuAdapter(this, menuHeaderList, menuChildMap)
        expandableMenu.setAdapter(sideMenuAdapter)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
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
}