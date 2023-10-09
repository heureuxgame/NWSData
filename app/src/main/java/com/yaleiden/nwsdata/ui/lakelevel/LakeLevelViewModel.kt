package com.yaleiden.nwsdata.ui.lakelevel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LakeLevelViewModel: ViewModel() {

    private val TAG: String = "LakeLevelViewModel"
    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

}