package com.arad.aview.ui.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arad.aview.R
import com.arad.aview.databinding.FragmentIntroBinding
import com.arad.aview.util.Constance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {


    lateinit var binding: FragmentIntroBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentIntroBinding.bind(view)
        super.onViewCreated(binding.root, savedInstanceState)
        checkNetworkNavigation(doWork = {
            binding.apply {
                poorConnection.isVisible = true
                txtPoorConnection.isVisible = true
            }
        })
        checkConnection()
    }

    private fun checkConnection() {
        binding.poorConnection.setOnClickListener {
            checkNetworkNavigation(doWork = {
                Toast.makeText(requireContext(), "اینترنت خود را بررسی کنید", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }


    private fun checkNetworkNavigation(doWork: () -> Unit) {
        if (Constance.checkNetwork(requireContext())) {
            lifecycleScope.launchWhenStarted {
                binding.progessbar.isVisible = true
                delay(3000)
                findNavController().navigate(R.id.action_introFragment_to_mainFragment)
            }
        } else {
            doWork()
        }

    }

}