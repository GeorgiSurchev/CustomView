package com.example.customview.cv.bubbleView

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import kotlin.math.hypot

/**
 * Used to calculate shapes's width and height and centers,
 * based on circle and rectangle shapes
 */
class Calculator(
	activity: Activity,
	focusShape: FocusShape,
	view: View?,
	radiusFactor: Double,
	fitSystemWindows: Boolean
) {

	/**
	 * @return Width of background bitmap
	 */
	private val bitmapWidth: Int

	/**
	 * @return Height of background bitmap
	 */
	private val bitmapHeight: Int

	/**
	 * @return Shape of focus
	 */
	var focusShape: FocusShape? = null
		private set

	/**
	 * @return Focus width
	 */
	var focusWidth: Int = 0
		private set

	/**
	 * @return Focus height
	 */
	var focusHeight: Int = 0
		private set

	/**
	 * @return X coordinate of focus circle
	 */
	var circleCenterX: Int = 0
		private set

	/**
	 * @return Y coordinate of focus circle
	 */
	var circleCenterY: Int = 0
		private set

	/**
	 * @return Radius of focus circle
	 */
	var viewRadius: Int = 0
		private set
	private var mHasFocus: Boolean = false
	private var windowFlags: Int = activity.window.attributes.flags

	init {
		val displayMetrics = DisplayMetrics()
		activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
		val deviceWidth = displayMetrics.widthPixels
		val deviceHeight = displayMetrics.heightPixels
		bitmapWidth = deviceWidth
		bitmapHeight = deviceHeight - if (fitSystemWindows) 0 else getStatusBarHeight(activity)
		val shouldAdjustYPosition = (fitSystemWindows && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				|| (isFullScreen() && !fitSystemWindows))
		if (view != null) {
			val adjustHeight = if (shouldAdjustYPosition) 0 else getStatusBarHeight(activity)
			val viewPoint = IntArray(2)
			view.getLocationInWindow(viewPoint)
			focusWidth = view.width
			focusHeight = view.height
			this.focusShape = focusShape
			circleCenterX = viewPoint[0] + focusWidth / 2
			circleCenterY = viewPoint[1] + focusHeight / 2 - adjustHeight
			viewRadius = ((hypot(view.width.toDouble(), view.height.toDouble()) / 2).toInt() * radiusFactor).toInt()
			mHasFocus = true
		} else {
			mHasFocus = false
		}
	}

	/**
	 * Setting circle focus at specific position
	 *
	 * @param positionX       focus at specific position Y coordinate
	 * @param positionY       focus at specific position circle radius
	 * @param radius          focus at specific position circle radius
	 */

	fun setCirclePosition(positionX: Int, positionY: Int, radius: Int) {
		circleCenterX = positionX
		viewRadius = radius
		circleCenterY = positionY
		focusShape = FocusShape.CIRCLE
		mHasFocus = true
	}

	/**
	 * @return True if there is a view to focus
	 */
	fun hasFocus(): Boolean {
		return mHasFocus
	}

	/**
	 * @param animCounter    Counter for circle animation
	 * @param animMoveFactor Move factor for circle animation (Bigger value makes bigger animation)
	 * @return Radius of animating circle
	 */
	fun circleRadius(animCounter: Int, animMoveFactor: Double): Float {
		return (viewRadius + animCounter * animMoveFactor).toFloat()
	}

	private fun isFullScreen(): Boolean = (windowFlags and WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0

	companion object {

		fun getStatusBarHeight(context: Context): Int {
			var result = 0
			val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
			if (resourceId > 0) {
				result = context.resources.getDimensionPixelSize(resourceId)
			}
			return result
		}
	}
}

enum class FocusShape {
	CIRCLE,
	ROUNDED_RECTANGLE
}
