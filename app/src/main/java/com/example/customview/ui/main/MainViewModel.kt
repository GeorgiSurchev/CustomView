package com.example.customview.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.cv.bubbleview.ShowCaseBubbleModel
import com.example.customview.cv.currencyconverter.CurrencyConverterModel

private const val FIRST_PAGE_TITLE = "BGN LEV"
private const val SECOND_PAGE_TITLE = "EURO"
private const val DEFAULT_PREFIX_SYMBOL = "$"
const val DEFAULT_RESULT_NAME = "BGN"
private const val INPUT_TEXT_HINT = "Input"
private const val RESULT_TEXT_HINT = "Result"
const val DEFAULT_SPINNER_INPUT_TEXT = "US Dollar"
private const val DEFAULT_SPINNER_TEXT_HINT = "Select currency"
private const val BUBBLE_FIRST_TITLE_TEXT = "This is currency converter"
private const val BUBBLE_FIRST_SUBTITLE_TEXT = "Write number of currency here"
private const val BUBBLE_SECOND_TITLE_TEXT = "Your have two option - lev or Euro"
private const val BUBBLE_TITLE_BODY =
	"When you cliched in prefix currency will be change randomly and the new result will automatically appear"
private const val BLUE_BUTTON_TEXT = "I agree"
private const val GRAY_BUTTON_TEXT = "Cancel"

class MainViewModel : ViewModel() {

	val spinnerInputText = MutableLiveData<String>(DEFAULT_SPINNER_INPUT_TEXT)

	val spinnerTextColor = MutableLiveData<Int>()

	val spinnerHintText = MutableLiveData<String>(DEFAULT_SPINNER_TEXT_HINT)

	val currencyConverterModel = CurrencyConverterModel(
		prefix = DEFAULT_PREFIX_SYMBOL,
		inputTextHint = INPUT_TEXT_HINT,
		resultTextHint = RESULT_TEXT_HINT,
		resultName = DEFAULT_RESULT_NAME
	)

	fun getShowCaseBubbleViewModel() = ShowCaseBubbleModel(
		firstTitleText = BUBBLE_FIRST_TITLE_TEXT,
		firstSubtitle = BUBBLE_FIRST_SUBTITLE_TEXT,
		secondTitle = BUBBLE_SECOND_TITLE_TEXT,
		textBody = BUBBLE_TITLE_BODY,
		blueButtonText = BLUE_BUTTON_TEXT,
		grayButtonText = GRAY_BUTTON_TEXT
	)

	fun listOfCurrencyPerUnitInLeva() = listOf(
		1.26821,
		0.285994,
		1.28462,
		1.76998,
		0.251263,
		0.0748156,
		0.263018,
		2.26283,
		0.209635,
		0.25794,
		0.00537759,
		0.492652,
		0.0221601,
		0.0128588,
		0.015211,
		0.00144409,
		0.0786194,
		0.400752,
		0.19071,
		1.1807,
		0.0335258,
		0.431541,
		0.401097,
		0.0220528,
		0.192883,
		1.22117,
		0.0537301,
		0.221873,
		1.62606,
		0.107847
	)
}

