package ltd.royalgreen.pacecloud.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.aboutmodule.AboutFragment
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardFragment
import ltd.royalgreen.pacecloud.loginmodule.*
import ltd.royalgreen.pacecloud.paymentmodule.PaymentFosterWebViewFragment
import ltd.royalgreen.pacecloud.paymentmodule.PaymentFragment
import ltd.royalgreen.pacecloud.paymentmodule.bkash.BKashPaymentWebViewFragment
import ltd.royalgreen.pacecloud.servicemodule.ServiceFragment
import ltd.royalgreen.pacecloud.supportmodule.SupportFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeFaqsFragment(): FaqsFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyFragment(): PrivacyFragment

    @ContributesAndroidInjector
    abstract fun contributeContactFragment(): ContactFragment

    @ContributesAndroidInjector
    abstract fun contributeSignUpDialogFragment(): SignUpDialog

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeServiceFragment(): ServiceFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): PaymentFragment

    @ContributesAndroidInjector
    abstract fun contributeBKashPaymentWebViewFragment(): BKashPaymentWebViewFragment

    @ContributesAndroidInjector
    abstract fun contributeSupportFragment(): SupportFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFosterFragment(): PaymentFosterWebViewFragment
}
