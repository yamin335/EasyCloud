package ltd.royalgreen.pacecloud.dinjectors

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class PaceCloudApp : Application(), HasActivityInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>
    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun activityInjector() = androidInjector
}