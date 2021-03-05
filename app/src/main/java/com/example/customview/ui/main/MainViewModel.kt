package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.bubbleView.ShowCaseBubbleModel
import com.example.customview.cv.bubbleView.ShowCaseBubbleViewModel
import com.example.customview.cv.edittextview.PrefixEditTextViewModel
import com.example.customview.cv.edittextview.PrefixViewModel

class MainViewModel : ViewModel() {

	val text = MutableLiveData("hello")

	val codeInputText = MutableLiveData<String>()

	val prefix = PrefixEditTextViewModel(model = PrefixViewModel("$", "number", 0))

	fun getShowCaseBubbleViewModel() = ShowCaseBubbleViewModel(
		ShowCaseBubbleModel(
			newTextKey = "nice",
			subtitleKey = "nice",
			titleKey = "nice",
			textBodyKey = "very very nice",
			blueButtonTextKey = "blue button",
			grayButtonTextKey = "gray button"
		)
	)
}

