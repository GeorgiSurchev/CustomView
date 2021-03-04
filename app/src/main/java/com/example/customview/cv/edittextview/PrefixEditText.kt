package com.example.customview.cv.edittextview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.customview.R
import com.example.customview.cv.spinner.SpinarAdapter
import com.example.customview.databinding.PrefixedittextBinding
import com.example.customview.ui.main.MainFragment

class PrefixEditText @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

	companion object {

		private const val DEFAULT_PADDING = -1f
	}

	val binding: PrefixedittextBinding =
		PrefixedittextBinding.inflate(LayoutInflater.from(context), this, true)
	//	var binding = PrefixedittextBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
	//	private var defaultLeftPadding = DEFAULT_PADDING
	//	private var prefix = ""

	init {

		// Needed to obtain the StyledAttributes from the attrs.xml file
		//		context.obtainStyledAttributes(attributes, R.styleable.PrefixEditText).let {
		//
		//			// Assigning the value of the prefix in the xml to the prefix variable in this class
		//			prefix = it.getString(R.styleable.PrefixEditText_prefix).orEmpty()
		//			it.recycle()
		//		}
	}

	//	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
	//		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	//		calculatePrefix()
	//	}

	fun setViewModel(viewModel: PrefixEditTextViewModel?) {
		binding.prefixEditTextViewModel = viewModel
	}

	fun setListener(listener: IPrefixEditTextListener) {
		binding.prefixEditTextListener = listener
	}

	fun changeColor(color: Int, currency: MainFragment.Currency) {
		binding.inputPrefixText.text = currency.simbol
		binding.textresult.text = String.format("%.3f", binding.textinput.text.toString().toInt() * currency.bgn)
		binding.textresult.setTextColor(color)
	}

	fun changeTextColor(color: Int) {
		binding.textinput.setTextColor(color)
	}
}

//	private fun calculatePrefix() {
//
//		if (defaultLeftPadding == DEFAULT_PADDING) {
//
//			val widths = FloatArray(prefix.length)
//			paint.getTextWidths(prefix, widths)
//			var textWidth = 0f
//			for (w in widths) {
//				textWidth += w
//			}
//
//			defaultLeftPadding = compoundPaddingLeft.toFloat()
//
//			setPadding(
//
//				(textWidth + defaultLeftPadding).toInt(),
//				paddingRight, paddingTop, paddingBottom
//			)
//		}
//	}

//	override fun onDraw(canvas: Canvas) {
//		super.onDraw(canvas)
//		canvas.drawText(
//			prefix, defaultLeftPadding,
//			getLineBounds(0, null).toFloat(), paint
//		)
//	}

