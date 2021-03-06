package com.example.customview.cv.viewpager

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * A view pager with customizable page scroll animation speed
 * Note: it use reflection to replace view pager scroller instance to be able to adjust scroll speed.
 */
private const val DEFAULT_DURATION = 250

class ViewPagerCustomDuration @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

	private var mScroller: FixedSpeedScroller? = null

	init {
		setScroller()
	}

	/**
	 * Override the Scroller instance with our own class so we can change the duration
	 */
	private fun setScroller() {
		try {
			val scroller = ViewPager::class.java.getDeclaredField("mScroller")
			scroller.isAccessible = true
			val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
			interpolator.isAccessible = true
			mScroller = FixedSpeedScroller(context, interpolator.get(null) as Interpolator)
			scroller.set(this, mScroller)
		} catch (e: NoSuchFieldException) {
			Log.d("ViewPagerCustomDuration", "Custom scroller initialization failed", e)
		} catch (e: IllegalAccessException) {
			Log.d("ViewPagerCustomDuration", "Custom scroller initialization failed", e)
		}
	}

	/**
	 * Set the scroll duration in millis
	 */
	fun setScrollDuration(duration: Int) {
		mScroller?.scrollDuration = duration
	}

	private class FixedSpeedScroller @JvmOverloads constructor(
		context: Context, interpolator: Interpolator? = null, flywheel: Boolean = true
	) : Scroller(context, interpolator, flywheel) {

		var scrollDuration = DEFAULT_DURATION

		override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
			// ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, scrollDuration)
		}
	}
}