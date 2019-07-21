package ltd.royalgreen.pacecloud.dinjectors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import ltd.royalgreen.pacecloud.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import ltd.royalgreen.pacecloud.loginmodule.SHARED_PREFS_KEY
import ltd.royalgreen.pacecloud.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
object AppModule {
    @Singleton
    @Provides
    @JvmStatic
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("http://123.136.26.98:8081")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
    }
}
