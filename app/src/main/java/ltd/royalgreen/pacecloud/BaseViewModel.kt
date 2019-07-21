package ltd.royalgreen.pacecloud

import androidx.lifecycle.ViewModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardViewModel
import ltd.royalgreen.pacecloud.dinjectors.AppModule
import ltd.royalgreen.pacecloud.dinjectors.DaggerViewModelInjector
import ltd.royalgreen.pacecloud.dinjectors.ViewModelInjector
import ltd.royalgreen.pacecloud.loginmodule.LoginViewModel

abstract class BaseViewModel : ViewModel() {
    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .appModule(AppModule)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is LoginViewModel -> injector.inject(this)
            is DashboardViewModel -> injector.inject(this)
        }
    }
}