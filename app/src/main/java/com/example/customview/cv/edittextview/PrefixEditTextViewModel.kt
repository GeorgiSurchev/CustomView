package com.example.customview.cv.edittextview

class PrefixEditTextViewModel(val model: PrefixViewModel) {

	val prefix: String
		get() = model.prefix

	val inputTextHint: String
		get() = model.inputTextHint

	val color: Int
		get() = model.color
}

data class PrefixViewModel(
	val prefix: String = "",
	val inputTextHint: String = "",
	val color: Int = 0,
)