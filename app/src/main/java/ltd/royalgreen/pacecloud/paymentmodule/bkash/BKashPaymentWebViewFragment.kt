package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import kotlinx.android.synthetic.main.payment_bkash_webview_fragment.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class BKashPaymentWebViewFragment: Fragment(), Injectable {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    val args: BKashPaymentWebViewFragmentArgs by navArgs()

    private var request = ""

    private val viewModel: BKashPaymentFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(BKashPaymentFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            if (mWebView.canGoBack()) {
                mWebView.goBack()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_bkash_webview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val checkout = args.CheckoutModel

        val paymentRqst = PaymentRequest()
        paymentRqst.amount = checkout.amount
        paymentRqst.intent = checkout.intent

        request = Gson().toJson(paymentRqst)

        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        //Below part is for enabling webview settings for using javascript and accessing html files and other assets

        mWebView.isClickable = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.setAppCacheEnabled(false)
        mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView.clearCache(true)
        mWebView.settings.allowFileAccessFromFileURLs = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true

        //To control any kind of interaction from html file

        mWebView.addJavascriptInterface( JavaScriptInterface(requireContext()), "AndroidNative")

        mWebView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                handler.proceed()
            }

//            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
//                val url = request?.url
//                val status = url?.fragment?.split("?")
//                val host = "pacecloud.com"
//                status?.let {
//                    if (host == url.host) {
//                        val paymentStatus = it[1].split("=")
//                        if (paymentStatus[0] == "paymentStatus" && paymentStatus[1] == "true") {
////                            viewModel.rechargeSuccessFailureStatus.postValue(paymentStatus[1])
//                        } else {
////                            viewModel.rechargeSuccessFailureStatus.postValue(paymentStatus[1])
//                        }
//                    }
//                }
//                return super.shouldInterceptRequest(view, request)
//            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (progressBar != null) {
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                val paymentRequest = "{paymentRequest:$request}"
                mWebView.loadUrl("javascript:callReconfigure($paymentRequest )")
                mWebView.loadUrl("javascript:clickPayButton()")
                if (progressBar != null) {
                    progressBar.visibility = View.GONE
                }
            }
        }

        mWebView.loadUrl("file:///android_asset/www/checkout_120.html")
    }
}