package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.payment_bkash_web_fragment.*
import kotlinx.android.synthetic.main.toast_custom_red.view.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.network.ApiService
import javax.inject.Inject

class BKashPaymentWebFragment : Fragment(), Injectable {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private var request = ""

    private var createBkash: CreateBkashModel? = null

    private var paymentRequest: PaymentRequest? = null
    val args: BKashPaymentWebFragmentArgs by navArgs()

    private val viewModel: BKashPaymentFragmentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(BKashPaymentFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_bkash_web_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
            findNavController().popBackStack()
        }
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
                val parent: ViewGroup? = null
                val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val toastView = inflater.inflate(R.layout.toast_custom_red, parent)
                toastView.message.text = it.second
                toast.view = toastView
                toast.show()

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
            val parent: ViewGroup? = null
            val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_LONG)
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflater.inflate(R.layout.toast_custom_green, parent)
            toastView.message.text = viewModel.bKashPaymentStatus.value?.second
            toast.view = toastView
            toast.show()

            findNavController().popBackStack()
        }

    }
}
