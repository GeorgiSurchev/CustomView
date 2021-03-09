package com.example.customview.cv.bubbleview

/**
 * A click listener for when [ShowCaseBubbleView] buttons are clicked.
 */
interface ShowCaseBubbleListener {

	/**
	 * Invoked when the [ShowCaseBubbleView]'s gray button is clicked.
	 */
	fun onGrayButtonClick()

	/**
	 * Invoked when the [ShowCaseBubbleView]'s blue button is clicked.
	 */
	fun onBlueButtonClick()
}
