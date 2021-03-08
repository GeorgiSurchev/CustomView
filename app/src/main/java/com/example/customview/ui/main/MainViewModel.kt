package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.bubbleview.ShowCaseBubbleModel
import com.example.customview.cv.currencyconverter.CurrencyConverterModel

class MainViewModel : ViewModel() {

	val spinnerInputText = MutableLiveData<String>("US Dollar")

	val spinnerTextColor = MutableLiveData<Int>()

	val spinnerHintText = MutableLiveData<String>("input your currency name here")

	val currencyConverterModel = CurrencyConverterModel("$", "input","result","BGN")

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

