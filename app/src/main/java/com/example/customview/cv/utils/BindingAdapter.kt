package com.example.customview.cv.utils

import android.widget.AutoCompleteTextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.customview.R

object BindingAdapter {

	@JvmStatic
	@BindingAdapter("setColor")
	fun color(view: AutoCompleteTextView, @ColorInt color: Int) {
		val inputColor = if (color == 0)  ContextCompat.getColor(view.context, R.color.black) else color
		view.setTextColor(inputColor)
	}
}