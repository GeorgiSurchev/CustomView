package com.example.customview.cv.currencyconverter

class PrefixEditTextViewModel(val model: PrefixViewModel) {

	val prefix: String
		get() = model.prefix

	val inputTextHint: String
		get() = model.inputTextHint
}

data class PrefixViewModel(
	val prefix: String = "",
	val inputTextHint: String = ""
)