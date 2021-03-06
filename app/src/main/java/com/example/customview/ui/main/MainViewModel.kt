package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.bubbleview.ShowCaseBubbleModel
import com.example.customview.cv.currencyconverter.PrefixEditTextViewModel
import com.example.customview.cv.currencyconverter.PrefixViewModel

class MainViewModel : ViewModel() {

	val spinnerInputText = MutableLiveData<String>()

	val spinnerTextColor = MutableLiveData<Int>()

	val prefix = PrefixEditTextViewModel(model = PrefixViewModel("$", "input"))

	fun getShowCaseBubbleViewModel() = ShowCaseBubbleModel(
		firstTitleText = "nice",
		firstSubtitle = "nice",
		secondTitle = "nice",
		textBody = "very very nice",
		blueButtonText = "blue button",
		grayButtonText = "gray button"
	)

	fun getTabNames(): List<String> = listOf("First Page","Second Page")
}

