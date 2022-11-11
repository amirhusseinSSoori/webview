package com.arad.aview.ui.main

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.arad.aview.R
import com.arad.aview.databinding.FragmentMainBinding
import com.arad.aview.ui.main.bind.MainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val viewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentMainBinding
    lateinit var callback: OnBackPressedCallback

    lateinit var bind: MainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = MainBinding(requireContext())

        onObserve()
        onCustomBackPressed()





    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        viewModel.onSubscribe(this)
        bind.webView(binding)
        bind.refresh(binding)

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

}