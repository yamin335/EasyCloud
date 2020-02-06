package ltd.royalgreen.pacecloud.dashboardmodule

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.network.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(private val apiService: ApiService,
                                              private val preferences: SharedPreferences) {

    suspend fun osStatusRepo(): Response<DashOsStatus> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
            addProperty("values", "cloudvmstatus")
            addProperty("UserID", user.resdata?.loggeduser?.userID)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getDashboardChartPortal(param)
        }
    }

    suspend fun osSummaryRepo(): Response<DashOsSummary> {
        val user = Gson().fromJson(preferences.getString("LoggedUser", null), LoggedUser::class.java)
        val jsonObject = JsonObject().apply {
            addProperty("CompanyID", user.resdata?.loggeduser?.companyID)
            addProperty("values", "cloudvm")
            addProperty("UserID", user.resdata?.loggeduser?.userID)
        }
        val param = JsonArray().apply {
            add(jsonObject)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.getDashboardChartPortalSummery(param)
        }
    }
}