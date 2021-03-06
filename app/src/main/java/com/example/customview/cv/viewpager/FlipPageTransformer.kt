package com.example.customview.cv.viewpager

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * A [ViewPager.PageTransformer], which simulated card flip animation in for page changes
 */
private const val HALF_PROGRESS = 0.5f

class FlipPageTransformer : ViewPager.PageTransformer {

	override fun transformPage(page: View, position: Float) {
		val progress = abs(position)
		val isVisible = progress < HALF_PROGRESS
		if (isVisible) {
			// position is -1, when page is hidden to left, 1 when is hidden to right and 0 when page is fully visible
			// so scaleX should be calculated from position 0 to 1m from position 1 or -1 to -1
			// below function do that
			page.scaleX = 1 - (progress * 2)

			// translate page in opposite direction, to compensate for view pager default translation
			page.translationX = -(page.width * position)
			page.alpha = 1f
		} else {
			page.alpha = 0f
			page.translationX = 0f
		}
	}
}