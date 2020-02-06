package ltd.royalgreen.pacecloud.loginmodule

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.network.*
import ltd.royalgreen.pacecloud.util.isNetworkAvailable
import ltd.royalgreen.pacecloud.util.showErrorToast
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun loginRepo(userName: String, password: String): Response<LoggedUser> {

        val jsonObject = JsonObject().apply {
            addProperty("userName", userName)
            addProperty("userPass", password)
        }

        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.loginportalusers(param)
        }
    }

    suspend fun signUpRepo(jsonObject: JsonObject): Response<String> {
        val param = JsonArray().apply {
            add(jsonObject)
        }

        return withContext(Dispatchers.IO) {
            apiService.register(param)
        }
    }
}