package ltd.royalgreen.pacecloud.dinjectors

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class PaceCloudApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun androidInjector() = androidInjector
}