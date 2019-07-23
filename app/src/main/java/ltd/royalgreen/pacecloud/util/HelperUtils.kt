package ltd.royalgreen.pacecloud.util

import android.content.Context
import android.net.ConnectivityManager

fun isNetworkAvailable(context: Context): Boolean {
    val connMngr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connMngr.activeNetworkInfo?.isConnected == true
}