package com.example.customview.cv.currencyconverter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.databinding.CurrencyConverterLayoutBinding
import com.example.customview.ui.main.MainFragment

class CurrencyConverter @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	val binding: CurrencyConverterLayoutBinding =
		CurrencyConverterLayoutBinding.inflate(LayoutInflater.from(context), this, true)

	fun setViewModel(viewModel: PrefixEditTextViewModel?) {
		binding.prefixEditTextViewModel = viewModel
	}

	fun setListener(listener: ICurrencyConverter) {
		binding.prefixEditTextListener = listener
	}

	fun shouldUpdateFields(color: Int, currency: MainFragment.Currency, convertToEU: Boolean = false): Boolean {
		val text = binding.currencyConverterInputNumber.text
		if (text.isNullOrBlank()) return false
		val convertResult = if (convertToEU) {
			String.format("%.3f", text.toString().toDouble() * currency.bgn * 0.51)
		} else {
			String.format("%.3f", text.toString().toDouble() * currency.bgn)
		}

		binding.currencyConverterPrefix.setTextColor(color)
		binding.currencyConverterPrefix.text = currency.symbol
		binding.currencyConverterResult.text = convertResult
		binding.currencyConverterResult.setTextColor(color)
		return true
	}

	fun changeTextColor(color: Int) {
		binding.currencyConverterInputNumber.setTextColor(color)
	}
}
