package ltd.royalgreen.pacecloud.dinjectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.royalgreen.pacecloud.aboutmodule.AboutFragmentViewModel
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivityViewModel
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardViewModel
import ltd.royalgreen.pacecloud.loginmodule.*
import ltd.royalgreen.pacecloud.paymentmodule.FosterPaymentViewModel
import ltd.royalgreen.pacecloud.paymentmodule.PaymentFragmentViewModel
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BKashPaymentViewModel
import ltd.royalgreen.pacecloud.servicemodule.ServiceFragmentViewModel
import ltd.royalgreen.pacecloud.supportmodule.SupportFragmentViewModel
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
    @ViewModelKey(LoginFragmentViewModel::class)
    abstract fun bindLoginFragmentViewModel(loginFragmentViewModel: LoginFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FaqsFragmentViewModel::class)
    abstract fun bindFaqsFragmentViewModel(faqsFragmentViewModel: FaqsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrivacyFragmentViewModel::class)
    abstract fun bindPrivacyFragmentViewModel(privacyFragmentViewModel: PrivacyFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactFragmentViewModel::class)
    abstract fun bindContactFragmentViewModel(contactFragmentViewModel: ContactFragmentViewModel): ViewModel

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
    @IntoMap
    @ViewModelKey(PaymentFragmentViewModel::class)
    abstract fun bindPaymentFragmentViewModel(paymentFragmentViewModel: PaymentFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BKashPaymentViewModel::class)
    abstract fun bindBKashPaymentFragmentViewModel(bKashPaymentViewModel: BKashPaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FosterPaymentViewModel::class)
    abstract fun bindFosterPaymentViewModel(fosterPaymentViewModel: FosterPaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SupportFragmentViewModel::class)
    abstract fun bindSupportFragmentViewModel(supportFragmentViewModel: SupportFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutFragmentViewModel::class)
    abstract fun bindAboutFragmentViewModel(aboutFragmentViewModel: AboutFragmentViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PaceCloudViewModelFactory): ViewModelProvider.Factory
}
