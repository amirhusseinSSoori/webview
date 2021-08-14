package com.arad.aview.util

import android.content.Context
import android.view.KeyEvent
import android.webkit.*

class CallBack(val ctx: Context) : WebViewClient() {
    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return false
    }


//    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//
//        view!!.loadUrl(url!!)
//        return true
//    }

}