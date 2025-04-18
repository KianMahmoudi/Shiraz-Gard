package com.kianmahmoudi.android.shirazgard.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


object NetworkUtils {
    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}