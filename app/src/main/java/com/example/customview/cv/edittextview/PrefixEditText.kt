package com.example.customview.cv.edittextview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.databinding.PrefixedittextBinding
import com.example.customview.ui.main.MainFragment

class PrefixEditText @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	val binding: PrefixedittextBinding =
		PrefixedittextBinding.inflate(LayoutInflater.from(context), this, true)

	fun setViewModel(viewModel: PrefixEditTextViewModel?) {
		binding.prefixEditTextViewModel = viewModel
	}

	fun setListener(listener: IPrefixEditTextListener) {
		binding.prefixEditTextListener = listener
	}

	fun changeColor(color: Int, currency: MainFragment.Currency) {
		binding.inputPrefixText.text = currency.symbol
		binding.textresult.text = String.format("%.3f", binding.textinput.text.toString().toInt() * currency.bgn)
		binding.textresult.setTextColor(color)
	}

	fun changeTextColor(color: Int) {
		binding.textinput.setTextColor(color)
	}
}
