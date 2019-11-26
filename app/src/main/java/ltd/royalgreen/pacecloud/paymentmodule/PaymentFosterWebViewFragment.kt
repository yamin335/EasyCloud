package ltd.royalgreen.pacecloud.paymentmodule

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.payment_foster_webview_fragment.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class PaymentFosterWebViewFragment: Fragment(), Injectable {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    val args: PaymentFosterWebViewFragmentArgs by navArgs()

    private val viewModel: PaymentFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(PaymentFragmentViewModel::class.java)
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
        return inflater.inflate(R.layout.payment_foster_webview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.rechargeSuccessFailureStatus.observe(this, Observer { status ->
            if (status == "true") {
                preferences.edit().apply {
                    putString("paymentRechargeStatus", "true")
                    apply()
                }
                findNavController().popBackStack()
            } else {
                preferences.edit().apply {
                    putString("paymentRechargeStatus", "false")
                    apply()
                }
                findNavController().popBackStack()
            }
        })

        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        mWebView.isClickable = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.clearCache(false)
        mWebView.settings.allowFileAccessFromFileURLs = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = request?.url
                val status = url?.fragment?.split("?")
                val host = "pacecloud.com"
                status?.let {
                    if (host == url.host) {
                        val paymentStatus = it[1].split("=")
                        if (paymentStatus[0] == "paymentStatus" && paymentStatus[1] == "true") {
                            viewModel.rechargeSuccessFailureStatus.postValue(paymentStatus[1])
                        } else {
                            viewModel.rechargeSuccessFailureStatus.postValue(paymentStatus[1])
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (progressBar != null) {
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                if (progressBar != null) {
                    progressBar.visibility = View.GONE
                }
            }
        }

        args.FosterProcessLink?.let {
//            mWebView.loadUrl("https://demo.fosterpayments.com.bd/fosterpayments/validationalldata.php?payment_id=Fost9316872.29227452-94528-uIAjD")
            mWebView.loadUrl(it)
        }
    }
}