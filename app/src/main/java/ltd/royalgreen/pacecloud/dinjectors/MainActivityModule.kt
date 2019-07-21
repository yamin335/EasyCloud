package ltd.royalgreen.pacecloud.dinjectors

import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacecloud.MainActivity
import ltd.royalgreen.pacecloud.SplashActivity
import ltd.royalgreen.pacecloud.loginmodule.FragmentBuildersModule
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.loginmodule.LoginActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun splashActivity(): SplashActivity

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideLoggedUser(
            preference: SharedPreferences
        ): LoggedUser {
            val loggedUser = preference.getString("LoggedUser", null)
            return Gson().fromJson(loggedUser, LoggedUser::class.java)
        }
    }
}