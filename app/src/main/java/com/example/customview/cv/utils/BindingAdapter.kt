package com.example.customview.cv.utils

import android.widget.AutoCompleteTextView
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter

object BindingAdapter {

	@JvmStatic
	@BindingAdapter("setColor")
	fun color(view: AutoCompleteTextView, @ColorInt color: Int) {
		view.setTextColor(color)
	}
}