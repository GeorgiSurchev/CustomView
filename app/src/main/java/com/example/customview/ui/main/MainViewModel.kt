package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.bubbleView.ShowCaseBubbleModel
import com.example.customview.cv.edittextview.PrefixEditTextViewModel
import com.example.customview.cv.edittextview.PrefixViewModel

class MainViewModel : ViewModel() {

	val codeInputText = MutableLiveData<String>()

	val prefix = PrefixEditTextViewModel(model = PrefixViewModel("$", "number", 0))

	fun getShowCaseBubbleViewModel() = ShowCaseBubbleModel(
		firstTitleText = "nice",
		firstSubtitle = "nice",
		secondTitle = "nice",
		textBody = "very very nice",
		blueButtonText = "blue button",
		grayButtonText = "gray button"
	)
}

