package ltd.royalgreen.pacecloud.dinjectors

import dagger.Component
import ltd.royalgreen.pacecloud.dashboardmodule.DashboardViewModel
import ltd.royalgreen.pacecloud.loginmodule.LoginViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class]
)
interface ViewModelInjector {

    fun inject(loginViewModel: LoginViewModel)
    fun inject(dashboardViewModel: DashboardViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun appModule(appModule: AppModule): Builder
    }
}