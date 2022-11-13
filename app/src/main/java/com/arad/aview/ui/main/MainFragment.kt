package com.arad.aview.ui.main

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arad.aview.MainActivity
import com.arad.aview.R
import com.arad.aview.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val viewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentMainBinding
    lateinit var callback: OnBackPressedCallback
    lateinit var webSettings: WebSettings
    var baseUrl = "https://pharoma.ir/"

    private var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    val REQUEST_SELECT_FILE = 100
    private val FILECHOOSER_RESULTCODE = 1




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        onObserve()
        onCustomBackPressed()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        viewModel.onSubscribe(this)

        binding.web.apply {
            webSettings = settings
            webSettings.javaScriptEnabled = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.domStorageEnabled = true
            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
            setNetworkAvailable(true)
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

            loadUrl(baseUrl)



            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }

//        binding.web.setWebViewClient(WebClientListener(ctx))
        binding.web.setWebViewClient(xWebViewClient())

      binding.web.setWebChromeClient(object : WebChromeClient() {
              // For 3.0+ Devices (Start)
              // onActivityResult attached before constructor
              protected fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String?) {
                  mUploadMessage = uploadMsg
                  val i = Intent(Intent.ACTION_GET_CONTENT)
                  i.addCategory(Intent.CATEGORY_OPENABLE)
                  i.type = "image/*"
                  startActivityForResult(
                      Intent.createChooser(i, "File Browser"),
                      FILECHOOSER_RESULTCODE
                  )
              }

              // For Lollipop 5.0+ Devices
              override fun onShowFileChooser(
                  mWebView: WebView,
                  filePathCallback: ValueCallback<Array<Uri>>,
                  fileChooserParams: FileChooserParams
              ): Boolean {
                  if (uploadMessage != null) {
                      uploadMessage!!.onReceiveValue(null)
                      uploadMessage = null
                  }
                  uploadMessage = filePathCallback
                  var intent: Intent? = null
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                      intent = fileChooserParams.createIntent()
                  }
                  try {
                      startActivityForResult(intent, REQUEST_SELECT_FILE)
                  } catch (e: ActivityNotFoundException) {
                      uploadMessage = null
                      return false
                  }
                  return true
              }

              //For Android 4.1 only
               fun openFileChooser(
                  uploadMsg: ValueCallback<Uri>,
                  acceptType: String?,
                  capture: String?
              ) {
                  mUploadMessage = uploadMsg
                  val intent = Intent(Intent.ACTION_GET_CONTENT)
                  intent.addCategory(Intent.CATEGORY_OPENABLE)
                  intent.type = "image/*"
                  startActivityForResult(
                      Intent.createChooser(intent, "File Browser"),
                      FILECHOOSER_RESULTCODE
                  )
              }

               fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                  mUploadMessage = uploadMsg
                  val i = Intent(Intent.ACTION_GET_CONTENT)
                  i.addCategory(Intent.CATEGORY_OPENABLE)
                  i.type = "image/*"
                  startActivityForResult(
                      Intent.createChooser(i, "File Chooser"),
                      FILECHOOSER_RESULTCODE
                  )
              }
          })



        binding.web.callOnClick()

        refresh(binding)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.isEnabled = false
        callback.remove()
    }

    private fun onCustomBackPressed() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.web.canGoBack()) {
                    binding.web.goBack()
                } else {
                    requireActivity().finish()
                }
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun onObserve() {
        viewModel.networkObserve.observe(this, Observer {
            when (it) {
                true -> {
                    binding.web.reload()
                }
            }
        })

    }

    fun refresh(bind: FragmentMainBinding) {
        bind.apply {
            reload.setOnRefreshListener {
                reload.postDelayed(Runnable {
                    web.reload()
                    reload.isRefreshing = false
                }, 3000)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode === REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
                uploadMessage = null
            }
        } else if (requestCode === FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result: Uri? =
                if (data == null || resultCode !== RESULT_OK) null else data.getData()
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        }
    }

    private class xWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}