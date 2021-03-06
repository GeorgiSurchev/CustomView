package com.example.customview.cv.button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.withStyledAttributes
import com.example.customview.R

class CustomImageButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs,defStyleAttr) {

	private var cornerRadius = 0f
	private var borderWidth = 0f
	private var startColor = 0
	private var centerColor = 0
	private var endColor = 0

	private val path = Path()
	private val borderPaint = Paint().apply {
		style = Paint.Style.FILL
	}

	init {
		//Get the values you set in xml
		context.withStyledAttributes(attrs, R.styleable.StyledButton) {
			borderWidth = getDimension(R.styleable.StyledButton_borderWidth, 10f)
			cornerRadius = getDimension(R.styleable.StyledButton_cornerRadius, 10f)
			startColor = getColor(R.styleable.StyledButton_startColor, Color.WHITE)
			centerColor = getColor(R.styleable.StyledButton_centerColor, Color.RED)
			endColor = getColor(R.styleable.StyledButton_endColor, Color.BLACK)
		}
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)

		// Create and set your gradient here so that the gradient size is always correct
		borderPaint.shader = LinearGradient(
			0f,
			0f,
			width.toFloat(),
			height.toFloat(),
			intArrayOf(startColor, centerColor, endColor),
			null,
			Shader.TileMode.CLAMP
		)
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		//Remove inner section (that you require to be transparent) from canvas
		path.rewind()
		path.addRoundRect(
			borderWidth,
			borderWidth,
			width.toFloat() - borderWidth,
			height.toFloat() - borderWidth,
			cornerRadius - borderWidth / 2,
			cornerRadius - borderWidth / 2,
			Path.Direction.CCW
		)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			canvas.clipOutPath(path)
		}

		//Draw gradient on the outer section
		path.rewind()
		path.addRoundRect(
			0f,
			0f,
			width.toFloat(),
			height.toFloat(),
			cornerRadius,
			cornerRadius,
			Path.Direction.CCW
		)
		canvas.drawPath(path, borderPaint)
	}
}