package ltd.royalgreen.pacecloud.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardFragment
import ltd.royalgreen.pacecloud.servicemodule.ServiceFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeServiceFragment(): ServiceFragment

//    @ContributesAndroidInjector
//    abstract fun contributeSearchFragment(): SearchFragment
}