package com.example.customview.cv.currencyconverter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.R
import com.example.customview.databinding.CurrencyConverterLayoutBinding
import com.example.customview.ui.main.MainFragment

class CurrencyConverter @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	val binding: CurrencyConverterLayoutBinding =
		CurrencyConverterLayoutBinding.inflate(LayoutInflater.from(context), this, true)

	fun setViewModel(viewModel: CurrencyConverterModel?) {
		binding.currencyConverterModel = viewModel
	}

	fun setListener(listener: ICurrencyConverterListener) {
		binding.currencyConverterListener = listener
	}

	fun shouldUpdateFields(color: Int, currency: MainFragment.Currency, convertToEU: Boolean = false): Boolean {
		val text = binding.currencyConverterInputNumber.text
		if (text.isNullOrBlank()) return false
		val blueColor = context.resources.getColor(R.color.Blue)
		val redColor = context.resources.getColor(R.color.Red)
		val (levaOrEuroString, resultColor) = if (convertToEU) Pair("EUR", blueColor) else Pair("BGN", redColor)
		val resultInLeva = text.toString().toDouble() * currency.bgn
		val convertResult = if (convertToEU) {
			String.format("%.3f", resultInLeva * 0.51)
		} else {
			String.format("%.3f", resultInLeva)
		}
		binding.currencyConverterPrefix.text = currency.symbol
		binding.currencyConverterResult.text = convertResult
		binding.currencyConverterPrefix.setTextColor(color)
		binding.currencyConverterResult.setTextColor(color)
		binding.currencyConverterInputNumber.setTextColor(color)
		binding.currencyConverterResultName.text = levaOrEuroString
		binding.currencyConverterResultName.setTextColor(resultColor)
		binding.currencyConverterResult.setTextColor(resultColor)
		return true
	}

	fun changeTextColor(color: Int) {
		binding.currencyConverterInputNumber.setTextColor(color)
	}
}
