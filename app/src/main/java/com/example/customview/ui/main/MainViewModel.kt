package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.edittextview.PrefixEditTextViewModel
import com.example.customview.cv.edittextview.PrefixViewModel

class MainViewModel : ViewModel() {

	val text = MutableLiveData("hello")

	val codeInputText = MutableLiveData<String>()

	val prefix = PrefixEditTextViewModel(model = PrefixViewModel("$", "number", 0))
}

