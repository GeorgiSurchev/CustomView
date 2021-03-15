package com.example.customview.cv.utils

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
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

	/**
	 * Set content text to a text view, or hide the [TextView] if no text is provided
	 */
	@JvmStatic
	@BindingAdapter("textOrGone")
	fun setTextOrGone(textView: TextView, contentText: CharSequence?) {
		val hideView = contentText.isNullOrBlank()
		textView.visibility = if (hideView) View.GONE else View.VISIBLE
		textView.text = contentText
	}
}