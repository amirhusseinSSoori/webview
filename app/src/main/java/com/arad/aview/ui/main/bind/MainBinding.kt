package com.arad.aview.ui.main.bind

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import com.arad.aview.databinding.FragmentMainBinding
import com.arad.aview.util.CallBack
import javax.inject.Inject

class MainBinding @Inject constructor(val ctx: Context) : BindingDetails {
    lateinit var webSettings: WebSettings
   //var baseUrl = "192.168.0.5:6060"

    // username : appadmin
    //passworld  : afagh246
   var baseUrl = "https://pharoma.ir/"
    override fun webView(bind: FragmentMainBinding) {

        bind.web.apply {
            webSettings = settings
            webSettings.javaScriptEnabled = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.domStorageEnabled = true
            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
            setNetworkAvailable(true)
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    bind.apply {
                       prg.isVisible = newProgress != 100
                    }
                }
            }
            webViewClient = CallBack(ctx)
            loadUrl(baseUrl)



            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                ctx.startActivity(i)
            }
        }

        bind.web.evaluateJavascript("", ValueCallback {
            
        })
        bind.web.callOnClick()
    }

    override fun refresh(bind: FragmentMainBinding) {
        bind.apply {
            reload.setOnRefreshListener {
                reload.postDelayed(Runnable {
                    web.reload()
                    reload.isRefreshing = false
                }, 3000)
            }
        }

    }

    override fun getUrl(bind: FragmentMainBinding) {
        bind.web.url?.let {
            baseUrl = it
        }
    }

}