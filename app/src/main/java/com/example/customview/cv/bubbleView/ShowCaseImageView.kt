package com.example.customview.cv.bubbleView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView

/**
 * The class responsible for the drawing of the show case image view.
 * Yields full screen background with circular shapes around the focused view
 */
class ShowCaseImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, @AttrRes defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private lateinit var calculator: Calculator
    private var path: Path
    private var rectF: RectF
    private var backgroundPaint: Paint
    private var erasePaint: Paint
    private var circleBorderPaint: Paint
    private var lvl1Paint: Paint
    private var lvl2Paint: Paint
    private var lvl3paint: Paint
    private var lvl4paint: Paint
    private var bitmap: Bitmap? = null
    private val focusBorderColor = Color.TRANSPARENT
    private var bgColor = Color.TRANSPARENT
    private var focusBorderSize: Int = 0
	private var hasCircle = false
    private var animCounter = 0
    private var step = 1
    private var animMoveFactor = 1.0
    private var focusAnimationMaxValue: Int = 0
    private var focusAnimationStep: Int = 0
    var focusAnimationEnabled = true
        set(value) {
            animCounter = if (value) DEFAULT_ANIM_COUNTER else 0
            field = value
        }

	constructor(
		context: Context,
		hasCircle: Boolean
	) : this(context) {
		this.hasCircle = hasCircle
	}

    companion object {
        private const val DEFAULT_ANIM_COUNTER = 19
        private const val FULL_OPACITY = 0xFF//100%
        private const val OPACITY_15 = 0x26//15%
        private const val OPACITY_30 = 0x4D //30%
        private const val OPACITY_40 = 0x66 //40%
        private const val DELAY_REDRAW = 20L
        private const val DECREASE_RADIUS = 30
        private const val LEVEL4_RADIUS_ADDITION = 23
        private const val LEVEL3_RADIUS_ADDITION = 40
        private const val LEVEL2_RADIUS_ADDITION = 70
        private const val LEVEL1_RADIUS_ADDITION = 120
        @VisibleForTesting
        var DISABLE_ANIMATIONS_FOR_TESTING = false
    }

    /**
     * Initializations for background and paints
     */
    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        setWillNotDraw(false)
        setBackgroundColor(Color.TRANSPARENT)
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = bgColor
            alpha = FULL_OPACITY
        }
        erasePaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            alpha = FULL_OPACITY
            isAntiAlias = true
        }

        circleBorderPaint = Paint().apply {
            isAntiAlias = true
            color = focusBorderColor
            strokeWidth = focusBorderSize.toFloat()
            style = Paint.Style.STROKE
        }
        lvl1Paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            alpha = OPACITY_15
            style = Paint.Style.FILL
        }
        lvl2Paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            alpha = OPACITY_30
            style = Paint.Style.FILL
        }

        lvl3paint = Paint().apply {
            isAntiAlias = true
            color = Color.TRANSPARENT
            alpha = OPACITY_30
            style = Paint.Style.FILL
        }

        lvl4paint = Paint().apply {
            isAntiAlias = true
            color = Color.TRANSPARENT
            alpha = OPACITY_40
            style = Paint.Style.FILL
        }
        path = Path()
        rectF = RectF()
    }

    /**
     * Setting parameters for background an animation
     *
     * @param _backgroundColor background color
     * @param _calculator      calculator object for calculations
     */
    fun setParameters(_backgroundColor: Int, _calculator: Calculator) {
        bgColor = _backgroundColor
        animMoveFactor = 1.0
        calculator = _calculator
    }

    /**
     * Setting parameters for focus border
     *
     * @param focusBorderColor
     * @param _focusBorderSize
     */
    fun setBorderParameters(focusBorderColor: Int, _focusBorderSize: Int) {
        focusBorderSize = _focusBorderSize
        circleBorderPaint.apply {
            color = focusBorderColor
            strokeWidth = focusBorderSize.toFloat()
        }
    }

    /**
     * Draws background and moving focus area
     *
     * @param canvas draw canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(bgColor)
            }
        }
        canvas.drawBitmap(bitmap!!, 0f, 0f, backgroundPaint)
		if (calculator.hasFocus() && hasCircle) {
			drawCircle(canvas)
			if (focusAnimationEnabled && !DISABLE_ANIMATIONS_FOR_TESTING) {
				if (animCounter == focusAnimationMaxValue) {
					step = -1 * focusAnimationStep
				} else if (animCounter == 0) {
					step = focusAnimationStep
				}
				animCounter += step
				postDelayed({
					postInvalidate()
				}, DELAY_REDRAW)
			}
		}
    }

    /**
     * Draws focus circle
     *
     * @param canvas canvas to draw
     */
    private fun drawCircle(canvas: Canvas) {
        //this is the default radius of the center view
        val radius = calculator.circleRadius(DEFAULT_ANIM_COUNTER, animMoveFactor) - DECREASE_RADIUS
        //outside center view that doesn't move
        val lvl4Rad = radius + LEVEL4_RADIUS_ADDITION
        //recalculates what the new radius should be
        val calculatedRadius = calculator.circleRadius(animCounter, animMoveFactor) - DECREASE_RADIUS
        val lvl1Rad = calculatedRadius + LEVEL1_RADIUS_ADDITION
        val lvl2Rad = calculatedRadius + LEVEL2_RADIUS_ADDITION
        val lvl3Rad = calculatedRadius + LEVEL3_RADIUS_ADDITION

        val circleCenterX = calculator.circleCenterX.toFloat()
        val circleCenterY = calculator.circleCenterY.toFloat()
        path.apply {
            reset()
            canvas.drawCircle(circleCenterX, circleCenterY, lvl1Rad, lvl1Paint)
            canvas.drawCircle(circleCenterX, circleCenterY, lvl2Rad, lvl2Paint)
            canvas.drawCircle(circleCenterX, circleCenterY, lvl3Rad, erasePaint)
            canvas.drawCircle(circleCenterX, circleCenterY, lvl3Rad, lvl3paint)
            canvas.drawCircle(circleCenterX, circleCenterY, lvl4Rad, erasePaint)
            canvas.drawCircle(circleCenterX, circleCenterY, lvl4Rad, lvl4paint)
        }
        canvas.drawCircle(calculator.circleCenterX.toFloat(), circleCenterY,
                radius, erasePaint)
    }

    fun setFocusAnimationParameters(maxValue: Int, step: Int) {
        focusAnimationMaxValue = maxValue
        focusAnimationStep = step
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (bitmap != null && bitmap?.isRecycled == false) {
            bitmap?.recycle()
            bitmap = null
        }
    }
}
