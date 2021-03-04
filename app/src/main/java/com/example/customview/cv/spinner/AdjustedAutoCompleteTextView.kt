package com.example.customview.cv.spinner

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.example.customview.R

private const val MAX_ITEMS_SHOWN = 5

class AdjustedAutoCompleteTextView @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, @AttrRes defStyle: Int = R.attr.autoCompleteTextViewStyle
) : AppCompatAutoCompleteTextView(context, attrs, defStyle) {

	override fun onFilterComplete(count: Int) {
		val maxHeight = resources.getDimension(R.dimen.spinner_drop_down_max_height).toInt()
		dropDownHeight = if (count > MAX_ITEMS_SHOWN) maxHeight else ViewGroup.LayoutParams.WRAP_CONTENT
		super.onFilterComplete(count)
	}
}