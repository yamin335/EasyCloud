package ltd.royalgreen.pacecloud.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.mainactivitymodule.MainActivity
import ltd.royalgreen.pacecloud.mainactivitymodule.SplashActivity
import ltd.royalgreen.pacecloud.loginmodule.LoginActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun splashActivity(): SplashActivity
}