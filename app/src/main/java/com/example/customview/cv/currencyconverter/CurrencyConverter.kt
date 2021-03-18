package com.example.customview.cv.currencyconverter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.R
import com.example.customview.databinding.CurrencyConverterLayoutBinding
import com.example.customview.ui.main.DEFAULT_RESULT_NAME

private const val EURO_MULTIPLIER = 0.51
private const val FORMAT_STYLE = "%.3f"
private const val EURO_CURRENCY_CODE = "EUR"

class CurrencyConverter @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	val binding: CurrencyConverterLayoutBinding =
		CurrencyConverterLayoutBinding.inflate(LayoutInflater.from(context), this, true)

	fun setViewModel(viewModel: CurrencyConverterViewModel?) {
		binding.currencyConverterViewModel = viewModel
	}

	fun setListener(listener: ICurrencyConverterListener) {
		binding.currencyConverterListener = listener
	}

	fun updateFields(color: Int, currency: Currency, convertToEU: Boolean = false) {
		val text = binding.currencyConverterInputNumber.text
		if (text.isNullOrBlank()) return
		val blueColor = context.resources.getColor(R.color.Blue)
		val redColor = context.resources.getColor(R.color.Red)
		val resultInLeva = text.toString().toDouble() * currency.bgn
		val (levaOrEuroString, resultColor, convertResult) = if (convertToEU) {
			Triple(
				first = EURO_CURRENCY_CODE,
				second = blueColor,
				third = String.format(FORMAT_STYLE, resultInLeva * EURO_MULTIPLIER)
			)
		} else {
			Triple(
				first = DEFAULT_RESULT_NAME,
				second = redColor,
				third = String.format(FORMAT_STYLE, resultInLeva)
			)
		}

		binding.currencyConverterPrefix.text = currency.symbol
		binding.currencyConverterResult.text = convertResult
		binding.currencyConverterPrefix.setTextColor(color)
		binding.currencyConverterResult.setTextColor(color)
		binding.currencyConverterInputNumber.setTextColor(color)
		binding.currencyConverterResultName.text = levaOrEuroString
		binding.currencyConverterResultName.setTextColor(resultColor)
		binding.currencyConverterResult.setTextColor(resultColor)
	}

	fun isInputNumberNotEmpty() = binding.currencyConverterInputNumber.text.isNotBlank()

	fun changeInputNumberTextColor(color: Int) {
		binding.currencyConverterInputNumber.setTextColor(color)
	}

	fun setAllFieldsVisibilityExceptInput(visibility: Int) {
		binding.currencyConverterVisibility.visibility = visibility
	}
}
