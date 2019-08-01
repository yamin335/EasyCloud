package ltd.royalgreen.pacecloud.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.MainActivity
import ltd.royalgreen.pacecloud.SplashActivity
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