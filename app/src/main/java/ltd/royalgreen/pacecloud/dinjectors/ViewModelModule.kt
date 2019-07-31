package ltd.royalgreen.pacecloud.dinjectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.royalgreen.pacecloud.MainActivityViewModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardViewModel
import ltd.royalgreen.pacecloud.loginmodule.LoginViewModel
import ltd.royalgreen.pacecloud.servicemodule.ServiceFragmentViewModel
import ltd.royalgreen.pacecloud.util.PaceCloudViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(dashboardViewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ServiceFragmentViewModel::class)
    abstract fun bindServiceFragmentViewModel(serviceFragmentViewModel: ServiceFragmentViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PaceCloudViewModelFactory): ViewModelProvider.Factory
}
