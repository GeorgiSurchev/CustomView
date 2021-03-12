package com.example.customview.cv.currencyconverter

@FunctionalInterface
interface ICurrencyConverterListener {

	fun onPrefixClicked()
	fun onInputNumberClicked()
	fun onBGNButtonClicked()
	fun onEuroButtonClicked()
}