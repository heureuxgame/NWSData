package com.yaleiden.nwsdata.ui.radar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RadarViewModel : ViewModel() {
    private val TAG = "RadarViewModel"
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}