package ltd.royalgreen.pacecloud.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.aboutmodule.AboutFragment
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardFragment
import ltd.royalgreen.pacecloud.paymentmodule.PaymentFragment
import ltd.royalgreen.pacecloud.servicemodule.ServiceFragment
import ltd.royalgreen.pacecloud.supportmodule.SupportFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeServiceFragment(): ServiceFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): PaymentFragment

    @ContributesAndroidInjector
    abstract fun contributeSupportFragment(): SupportFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment
}
