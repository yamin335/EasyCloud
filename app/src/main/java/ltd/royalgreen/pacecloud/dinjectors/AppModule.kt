package ltd.royalgreen.pacecloud.dinjectors

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ltd.royalgreen.pacecloud.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoggedUser
import ltd.royalgreen.pacecloud.loginmodule.SHARED_PREFS_KEY
import ltd.royalgreen.pacecloud.network.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module(includes = [ViewModelModule::class])
@Suppress("unused")
object AppModule {
    @Singleton
    @Provides
    @JvmStatic
    fun provideApiService(applicationContext: Application): ApiService {
        val interceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("AuthorizedToken", "cmdsX3NlY3JldF9hcGlfa2V5")
                .build()
            chain.proceed(newRequest)
        }

//        val certificatePinner = CertificatePinner
//            .Builder()
//            .add("pacecloud.com", "sha256/V2M0GsUABUSAMcK8/O0KB8GQY32YdoyC6WDVmvR9hE=")
//            .build()

        val pair = getSSLSocketFactory(applicationContext)

        val clientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(7, TimeUnit.SECONDS)
            .callTimeout(7, TimeUnit.SECONDS)
            .addInterceptor(interceptor)

        pair?.let {
            clientBuilder.sslSocketFactory(it.first, trustManager = it.second)
        }

        val client = clientBuilder.build()

        //.certificatePinner(certificatePinner = certificatePinner)

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.pacecloud.com")
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

    @Throws(
        CertificateException::class,
        IOException::class,
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    private fun createCertificate(caInput: InputStream): Pair<SSLContext, X509TrustManager> {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        val cFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
        val certificate: X509Certificate = caInput.use {
            cFactory.generateCertificate(it) as X509Certificate
        }

        // creating a KeyStore containing our trusted CAs
        val keyStoreType: String = KeyStore.getDefaultType()
        val keyStore: KeyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
            setCertificateEntry("ca", certificate)
        }

        // creating a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }

        // creating an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }

        return Pair(sslContext, tmf.trustManagers[0] as X509TrustManager)
    }


    private fun getSSLSocketFactory(context: Context): Pair<SSLSocketFactory, X509TrustManager>? {
        var sslContext: SSLContext? = null
        var trustManager: X509TrustManager? = null
        try {
            val pair = createCertificate(context.resources.openRawResource(R.raw.pacecloud_ssl_certificate))
            sslContext = pair.first
            trustManager = pair.second
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return sslContext?.let { sslCtx ->
            trustManager?.let {
                Pair(sslCtx.socketFactory, it)
            }
        }
    }

//    @Singleton
//    @JvmStatic
//    @Provides
//    fun provideLoggedUser(preference: SharedPreferences): LoggedUser {
//        val loggedUser = preference.getString("LoggedUser", "{}")
//        return Gson().fromJson(loggedUser, LoggedUser::class.java)
//    }
}
