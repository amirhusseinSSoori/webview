package com.arad.aview.ui.main

import androidx.lifecycle.*
import com.arad.aview.ConnectivityLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var network: ConnectivityLiveData, val check: Boolean) :
    ViewModel() {

    var checkNewWork = MutableLiveData<Boolean>(false)
    var networkObserve: LiveData<Boolean> = checkNewWork


    init {
        checkNewWork.value = check
    }

    fun onSubscribe(context: LifecycleOwner) {
        viewModelScope.launch {
            network.observe(context, Observer {
                when (it) {
                    false -> {
                        checkNewWork.value = false
                    }
                    true -> {
                        checkNewWork.value = true
                    }
                }

            })
        }
    }

}