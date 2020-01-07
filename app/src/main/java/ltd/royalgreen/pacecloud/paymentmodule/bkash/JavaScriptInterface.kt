package ltd.royalgreen.pacecloud.paymentmodule.bkash

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface


class JavaScriptInterface(c: Context) {
    var mContext: Context = c
    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    fun switchActivity(response: String) {
        print("REQUEST: $response")
        Log.d("RESPONSE: ", response)
//        val intent = Intent(mContext, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        mContext.startActivity(intent)
//        return "{\"amount\":\'555\',\"intent\":\'authorization\'}"
    }

}