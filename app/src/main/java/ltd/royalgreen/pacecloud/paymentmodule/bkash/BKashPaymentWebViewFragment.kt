package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
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
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.payment_bkash_webview_fragment.mWebView
import kotlinx.android.synthetic.main.payment_bkash_webview_fragment.progressBar
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.util.showErrorToast
import ltd.royalgreen.pacecloud.util.showSuccessToast
import javax.inject.Inject

class BKashPaymentWebViewFragment: Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val args: BKashPaymentWebViewFragmentArgs by navArgs()

    private var request = ""

    private var createBkash: CreateBkashModel? = null

    private var paymentRequest: PaymentRequest? = null

    private val viewModel: BKashPaymentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(BKashPaymentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_bkash_webview_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        paymentRequest = args.PaymentRequestModel
        request = Gson().toJson(paymentRequest)
        createBkash = args.CreateBkashModel

        viewModel.resBkash.observe(this, Observer {
            val errorCode: String? = null
            val errorMessage: String? = null
            val jsonObject = JsonParser.parseString(it).asJsonObject.apply {
                addProperty("errorCode", errorCode)
                addProperty("errorMessage", errorMessage)
            }
            viewModel.bkashPaymentExecuteJson = jsonObject
            val jsonString = jsonObject.toString()
            mWebView.loadUrl("javascript:createBkashPayment($jsonString )")
        })

        viewModel.bKashPaymentStatus.observe(this, Observer {
            if (it.first) {
                mWebView.evaluateJavascript("javascript:finishBkashPayment()", null)
            } else {
                showErrorToast(requireContext(), it.second)

                findNavController().popBackStack()
            }
        })

        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        //Below part is for enabling webview settings for using javascript and accessing html files and other assets

        mWebView.isClickable = true
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.clearCache(false)
        mWebView.settings.allowFileAccessFromFileURLs = true
        mWebView.settings.allowUniversalAccessFromFileURLs = true

        //To control any kind of interaction from html file

//        mWebView.addJavascriptInterface( JavaScriptInterface(requireContext()), "AndroidNative")

        mWebView.addJavascriptInterface( JavaScriptWebViewInterface(requireContext()), "AndroidNative")

        mWebView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                handler.proceed()
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (progressBar != null) {
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                val paymentRequestJson = "{paymentRequest:$request}"
                mWebView.loadUrl("javascript:callReconfigure($paymentRequestJson )")
                mWebView.loadUrl("javascript:clickPayButton()")
                if (progressBar != null) {
                    progressBar.visibility = View.GONE
                }
            }
        }

        mWebView.loadUrl("file:///android_asset/www/checkout_120.html")
    }

    override fun onDestroy() {
        mWebView.removeJavascriptInterface("AndroidNative")
        super.onDestroy()
    }

    inner class JavaScriptWebViewInterface(context: Context) {
        var mContext: Context = context

         // Handle event from the web page
        @JavascriptInterface
        fun createPayment() {
             viewModel.createBkashCheckout(paymentRequest, createBkash)
             viewModel.bkashToken = createBkash?.authToken
        }

        @JavascriptInterface
        fun executePayment() {
            viewModel.executeBkashPayment()
        }

        @JavascriptInterface
        fun finishBkashPayment() {
            showSuccessToast(requireContext(), viewModel.bKashPaymentStatus.value?.second ?: "UNKNOWN Message!")

            findNavController().popBackStack()
        }

    }
}
