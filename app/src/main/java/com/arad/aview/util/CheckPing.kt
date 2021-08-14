package com.arad.aview.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

class CheckPing @Inject constructor(var cn: Context) {
    private var runtime: Runtime? = null
    private var onPingListener: OnCheckPingListener? = null
    private val scopeIo = CoroutineScope(Job() + Dispatchers.IO)


    init {
        runtime = Runtime.getRuntime()
    }

    fun setOnPingListener(onPingListener: OnCheckPingListener) {
        this.onPingListener = onPingListener
    }

    @Throws(InterruptedException::class)
    fun ping(destination: String?, timeoutInSeconds: Int): Boolean {
        try {
            val command =
                String.format(Constance.PING, timeoutInSeconds, destination)
            val process = runtime!!.exec(command)
            val ret = process.waitFor()
            process.destroy()
            return ret == 0
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }


    fun pingUntilSucceeded(destination: String?) {
        scopeIo.launch {
            try {

                if (isNetworkConnected(cn)) {
                    if (ping(destination, 1)) {
                        if (onPingListener != null) onPingListener!!.onPingStatus(Constance.PING_SUSSECC)

                    } else {
                        if (onPingListener != null) onPingListener!!.onPingStatus(Constance.PING_FAILED)
                    }
                } else {
                    if (onPingListener != null) onPingListener!!.onPingStatus(Constance.NO_INTERNET)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }


        }

    }


    interface OnCheckPingListener {
        suspend fun onPingStatus(status: String)
    }


    private fun isNetworkConnected(@ApplicationContext context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}