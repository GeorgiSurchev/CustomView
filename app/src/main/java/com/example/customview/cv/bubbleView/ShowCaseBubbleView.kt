package com.example.customview.cv.bubbleView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.R
import com.example.customview.databinding.ShowCaseBubbleLayoutBinding

class ShowCaseBubbleView @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, @AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	private val binding: ShowCaseBubbleLayoutBinding =
		ShowCaseBubbleLayoutBinding.inflate(LayoutInflater.from(context), this, true)

	private var viewModel: ShowCaseBubbleModel? = null
		set(value) {
			field = value
			binding.viewModel = field
		}

	constructor(
		context: Context,
		viewModel: ShowCaseBubbleModel,
		showCaseBubbleListener: ShowCaseBubbleListener?
	) : this(context) {
		this.viewModel = viewModel

		layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
			val startEndMargin = resources.getDimension(R.dimen.showcase_view_bubble_layout_margin_start_end).toInt()
			setMargins(startEndMargin, 0, startEndMargin, 0)
		}

		setClickListeners(showCaseBubbleListener)
	}

	private fun setClickListeners(listener: ShowCaseBubbleListener?) {
		binding.showCaseBubbleGrayButton.setOnClickListener {
			listener?.onGrayButtonClick()
		}
	}
}
