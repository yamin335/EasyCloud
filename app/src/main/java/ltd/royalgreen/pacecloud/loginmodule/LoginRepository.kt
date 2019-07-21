package ltd.royalgreen.pacecloud.loginmodule

import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService) {

    fun fetchLoginData(): Unit {

    }
}