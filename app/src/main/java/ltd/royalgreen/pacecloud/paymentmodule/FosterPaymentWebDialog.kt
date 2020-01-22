package ltd.royalgreen.pacecloud.paymentmodule

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacecloud.databinding.PaymentFosterWebDialogBinding
import ltd.royalgreen.pacecloud.dinjectors.Injectable
import ltd.royalgreen.pacecloud.util.autoCleared
import ltd.royalgreen.pacecloud.util.showErrorToast
import ltd.royalgreen.pacecloud.util.showSuccessToast
import javax.inject.Inject

class FosterPaymentWebDialog internal constructor(private val callBack: FosterPaymentCallback, private val fosterProcessUrl: String, private val fosterPaymentStatusUrl: String): DialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FosterPaymentViewModel by lazy {
        // Get the ViewModel.
        ViewModelProviders.of(this, viewModelFactory).get(FosterPaymentViewModel::class.java)
    }

    private var binding by autoCleared<PaymentFosterWebDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.payment_foster_web_dialog,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.goBack.setOnClickListener {
            if (binding.mWebView.canGoBack()) {
                binding.mWebView.goBack()
            } else {
                callBack.onFosterPaymentCancelled()
                dismiss()
            }
        }

        viewModel.rechargeSuccessFailureStatus.observe(this, Observer { status ->
            if (status) {
                showSuccessToast(requireContext(), "Payment Successful")
                callBack.onFosterPaymentSuccess()
                dismiss()
            } else {
                callBack.onFosterPaymentError()
                showErrorToast(requireContext(), "Payment not successful !")
                dismiss()
            }
        })

        viewModel.showMessage.observe(this, Observer { (type, message) ->
            if (type == "SUCCESS") {
                showSuccessToast(requireContext(), message)
//                viewModel.showMessage.postValue(Pair("null", ""))
            } else if (type == "ERROR") {
                showErrorToast(requireContext(), message)
//                viewModel.showMessage.postValue(Pair("null", ""))
            }
        })

        val webSettings: WebSettings = binding.mWebView.settings
        webSettings.javaScriptEnabled = true

        binding.mWebView.isClickable = true
        binding.mWebView.settings.domStorageEnabled = true
        binding.mWebView.settings.setAppCacheEnabled(true)
        binding.mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.mWebView.clearCache(false)
        binding.mWebView.settings.allowFileAccessFromFileURLs = true
        binding.mWebView.settings.allowUniversalAccessFromFileURLs = true

        binding.mWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val url = request?.url
                val status = url?.fragment?.split("?")
                val host = "pacecloud.com"
                status?.let {
                    if (host == url.host) {
                        val paymentStatus = it[1].split("=")
                        if (paymentStatus[0] == "paymentStatus" && paymentStatus[1] == "true") {
                            viewModel.checkFosterPaymentStatus(fosterPaymentStatusUrl)
                        } else {
                            viewModel.showMessage.postValue(Pair("ERROR", "Payment not successful !"))
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (binding.loader != null) {
                    binding.loader.visibility = View.VISIBLE
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                if (binding.loader != null) {
                    binding.loader.visibility = View.GONE
                }
            }
        }

        binding.mWebView.loadUrl(fosterProcessUrl)
    }

    interface FosterPaymentCallback {
        fun onFosterPaymentSuccess()
        fun onFosterPaymentError()
        fun onFosterPaymentCancelled()
    }
}