package com.example.customview.cv.bubbleview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.example.customview.R
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The class responsible for the behaviour of the [ShowCaseImageView]
 */
private const val FOCUS_ANIMATION_STEP = 5
private const val ANIMATION_DURATION = 400
private const val FOCUS_ANIM_MAX = 20
private const val INFORMATION_BUBBLE_ANIM_START_OFFSET = 100L
private const val INFORMATION_BUBBLE_ANIM_DURATION = 800L
private const val INFORMATION_BUBBLE_TRIANGLE_ROTATION = 180f

// Tag for container view
private const val CONTAINER_TAG = "ShowCaseViewTag"
@Suppress("LargeClass")
class ShowCaseView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	@AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

	//region fields
	private lateinit var activity: Activity
	private var hasCircle: Boolean = false
	private var customView: View? = null
	private var focusCircleRadiusFactor: Double = 1.0
	private var focusedView: View? = null
	private var clickableView: View? = null
	private var backgroundColorView: Int = 0
	private var closeOnTouch: Boolean = true
	private var enableTouchOnFocusedView: Boolean = false
	private var fitSystemWindows = true
	private var delay: Long = 0
	private val animationDuration = ANIMATION_DURATION
	private var focusAnimationMaxValue = FOCUS_ANIM_MAX
	private var focusAnimationStep: Int = FOCUS_ANIMATION_STEP
	private var animateInfoBubble = false
	private var centerX: Int = 0
	private var centerY: Int = 0
	private var root: ViewGroup? = null
	private var focusCalculator: Calculator? = null
	private var clickableCalculator: Calculator? = null
	private var focusPositionX: Int = 0
	private var focusPositionY: Int = 0
	private var focusCircleRadius: Int = 0
	private var focusRectangleWidth: Int = 0
	private var focusRectangleHeight: Int = 0
	private var focusAnimationEnabled: Boolean = true
	private var showCaseImageView: ShowCaseImageView? = null
	private var hasCircularEnterExitAnim = false
	private var showCaseBubbleViewModel: ShowCaseBubbleModel? = null
	private var stateListener: OnShowCaseStateListener? = null
	private var showCaseBubbleListener: ShowCaseBubbleListener? = null
	private var highlightedAreaClickListener: HighlightedAreaClickListener? = null
	var fadingIn = false
		private set
	var fadingOut = false
		private set
	val animating: Boolean
		get() = fadingIn || fadingOut
	//endregion

	private constructor(
		hasCircle: Boolean,
		customView: View?,
		activity: Activity,
		focusView: View?,
		clickableView: View?,
		focusCircleRadiusFactor: Double,
		backgroundColor: Int,
		closeOnTouch: Boolean,
		enableTouchOnFocusedView: Boolean,
		fitSystemWindows: Boolean,
		focusPositionX: Int,
		focusPositionY: Int,
		focusCircleRadius: Int,
		focusRectangleWidth: Int,
		focusRectangleHeight: Int,
		animationEnabled: Boolean,
		focusAnimationMaxValue: Int,
		focusAnimationStep: Int,
		animateInfoBubble: Boolean,
		delay: Long,
		hasCircularAnim: Boolean,
		showCaseBubbleModel: ShowCaseBubbleModel?,
		showCaseBubbleListener: ShowCaseBubbleListener?,
		stateListener:OnShowCaseStateListener?,
		highlightedAreaClickListener: HighlightedAreaClickListener?
	) : this(activity) {

		this.hasCircle = hasCircle
		this.customView = customView
		this.activity = activity
		this.focusedView = focusView
		this.clickableView = clickableView
		this.focusCircleRadiusFactor = focusCircleRadiusFactor
		this.backgroundColorView = backgroundColor
		this.closeOnTouch = closeOnTouch
		this.enableTouchOnFocusedView = enableTouchOnFocusedView
		this.fitSystemWindows = fitSystemWindows
		this.focusPositionX = focusPositionX
		this.focusPositionY = focusPositionY
		this.focusCircleRadius = focusCircleRadius
		this.focusRectangleWidth = focusRectangleWidth
		this.focusRectangleHeight = focusRectangleHeight
		this.focusAnimationEnabled = animationEnabled
		this.focusAnimationMaxValue = focusAnimationMaxValue
		this.focusAnimationStep = focusAnimationStep
		this.animateInfoBubble = animateInfoBubble
		this.delay = delay
		this.hasCircularEnterExitAnim = hasCircularAnim
		this.showCaseBubbleViewModel = showCaseBubbleModel
		this.showCaseBubbleListener = showCaseBubbleListener
		this.stateListener = stateListener
		this.highlightedAreaClickListener = highlightedAreaClickListener
		initializeParameters()
	}

	/**
	 * Calculates and set initial parameters
	 */
	private fun initializeParameters() {
		backgroundColorView = if (backgroundColorView != 0) {
			backgroundColorView
		} else {
			ContextCompat.getColor(activity, R.color.showcase_background)
		}
		val displayMetrics = DisplayMetrics()
		activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
		val deviceWidth = displayMetrics.widthPixels
		val deviceHeight = displayMetrics.heightPixels
		centerX = deviceWidth / 2
		centerY = deviceHeight / 2
	}

	/**
	 * Shows ShowCaseView
	 */
	fun show() {
		// if view is not laid out get width/height values in onGlobalLayout
		if (focusedView != null && focusedView?.width == 0 && focusedView?.height == 0) {
			focusedView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
		} else {
			focus()
		}
	}

	fun dismiss() {
		removeView()
	}

	private fun focus() {
		focusCalculator = Calculator(
			activity,
			FocusShape.CIRCLE,
			focusedView,
			focusCircleRadiusFactor,
			fitSystemWindows
		)

		clickableCalculator = Calculator(
			activity,
			FocusShape.CIRCLE,
			clickableView,
			focusCircleRadiusFactor,
			fitSystemWindows
		)

		val androidContent = activity.findViewById<View>(android.R.id.content) as ViewGroup
		root = androidContent.parent.parent as ViewGroup?
		root?.postDelayed(Runnable {
			if (activity.isFinishing) {
				return@Runnable
			}
			val visibleView = root?.findViewWithTag<View>(CONTAINER_TAG) as ShowCaseView?
			isClickable = !enableTouchOnFocusedView
			if (visibleView == null) {
				tag = CONTAINER_TAG
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
				root?.addView(this)
				setupTouchListener()
				setCalculatorParams()
				addFancyImageView()
				inflateContent()
				startEnterAnimation()
			}
			stateListener?.onShow()
		}, delay)
	}

	private fun setCalculatorParams() {
		focusCalculator?.apply {
			if (hasFocus()) {
				centerX = circleCenterX
				centerY = circleCenterY
			}
			if (focusCircleRadius > 0) {
				setCirclePosition(focusPositionX, focusPositionY, focusCircleRadius)
			}
		}
	}

	private fun addFancyImageView() {
		ShowCaseImageView(activity, hasCircle).apply {
			setFocusAnimationParameters(focusAnimationMaxValue, focusAnimationStep)
			setParameters(backgroundColorView, focusCalculator!!)
			focusAnimationEnabled = this@ShowCaseView.focusAnimationEnabled
			layoutParams = FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
			)
			addView(this)
		}
	}

	private fun inflateContent() {
		addInformationViews()
	}

	@SuppressLint("ClickableViewAccessibility")
	private fun setupTouchListener() {
		setOnTouchListener(OnTouchListener { _, event ->
			if (event.actionMasked == MotionEvent.ACTION_DOWN) {
				when {
					enableTouchOnFocusedView && isWithinZone(event, focusCalculator) -> {
						// Check if there is a clickable view within the focusable view
						// Let the touch event pass through to clickable zone only if clicking within,
						// otherwise return true to ignore event
						// If there is no clickable view we let through the click to the focusable view
						clickableView?.let {

							val isWithinClickZone = isWithinZone(event, clickableCalculator)
							if (isWithinClickZone) {
								onHighlightedAreaClick()
								removeView()
							}
						}
						return@OnTouchListener false
					}

					closeOnTouch -> hide()
				}
			}
			true
		})
	}

	private fun onHighlightedAreaClick() {
		if (fadingOut.not()) {
			highlightedAreaClickListener?.onHighlightedAreaClick()
		}
	}

	/**
	 * Check whether the event is within the provided zone that was already computed with the provided calculator
	 *
	 * @param event         The event from onTouch callback
	 * @param calculator    The calculator that holds the zone's position
	 */
	private fun isWithinZone(event: MotionEvent, calculator: Calculator?): Boolean {
		val x = event.x
		val y = event.y
		val focusCenterX = calculator?.circleCenterX ?: 0
		val focusCenterY = calculator?.circleCenterY ?: 0
		val focusRadius = calculator?.circleRadius(0, 1.0) ?: 0f
		val distance = sqrt((focusCenterX - x).toDouble().pow(2.0) + (focusCenterY - y).toDouble().pow(2.0))
		return abs(distance) < focusRadius
	}

	/**
	 * Starts enter animation of ShowCaseView
	 */
	private fun startEnterAnimation() {
		if (shouldShowCircularAnimation()) {
			doCircularEnterAnimation()
		} else {
			doFadeInAnimation()
		}
	}

	private fun doFadeInAnimation() {
		fadingIn = true
		val fadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.show_case_bubble_fade_in)
		fadeInAnimation.fillAfter = true
		fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
			override fun onAnimationEnd(animation: Animation) {
				fadingIn = false
			}

			override fun onAnimationRepeat(p0: Animation?) {}
			override fun onAnimationStart(p0: Animation?) {}
		})
		startAnimation(fadeInAnimation)
	}

	/**
	 * Hides ShowCaseView with animation
	 */
	private fun hide() {
		if (shouldShowCircularAnimation()) {
			doCircularExitAnimation()
		} else {
			doFadeOutAnimation()
		}
		stateListener?.onHide()
	}

	private fun doFadeOutAnimation() {
		if (fadingOut) return

		fadingOut = true
		val fadeOut = AnimationUtils.loadAnimation(activity, R.anim.show_case_bubble_fade_out)
		fadeOut.setAnimationListener(object : Animation.AnimationListener {
			override fun onAnimationEnd(animation: Animation) {
				removeView()
				fadingOut = false
			}

			override fun onAnimationRepeat(p0: Animation?) {}
			override fun onAnimationStart(p0: Animation?) {}
		})
		fadeOut.fillAfter = true
		startAnimation(fadeOut)
	}

	/**
	 * Adds the [ShowCaseBubbleView] and a triangle shaped view used to relay the information of the Extended tutorial.
	 * or [customView]
	 */
	private fun addInformationViews() {
		val initialPoint = centerY.toFloat()
		val triangleSize = resources.getDimension(R.dimen.showcase_view_triangle_width_height).toInt()
		val triangle = getTriangleView(triangleSize, initialPoint)
		addView(triangle)

		val customView = customView
		val view = if (customView != null) {
			customView
		} else {
			val bubbleViewModel = showCaseBubbleViewModel ?: return
			ShowCaseBubbleView(context, bubbleViewModel, getActualBubbleListener())
		}
		addView(view)
		setShowCaseInfoPosition(view, triangle, triangleSize, initialPoint)
	}

	private fun getActualBubbleListener() = object : ShowCaseBubbleListener {
		override fun onGrayButtonClick() {
			if (animating) return
			hide()
			showCaseBubbleListener?.onGrayButtonClick()
		}

		override fun onBlueButtonClick() {
			hide()
			showCaseBubbleListener?.onGrayButtonClick()
		}
	}

	private fun getTriangleView(size: Int, initialPoint: Float) = View(context).apply {
		background = ContextCompat.getDrawable(context, R.drawable.show_case_triangle)
		layoutParams = LayoutParams(size, size)
		if (initialPoint.isBelowDisplayCenter()) {
			rotation = INFORMATION_BUBBLE_TRIANGLE_ROTATION
		}
	}

	private fun setShowCaseInfoPosition(
		showCaseInfoView: View,
		triangleView: View,
		triangleSize: Int,
		initialPoint: Float
	) {
		showCaseInfoView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
			override fun onGlobalLayout() {
				showCaseInfoView.viewTreeObserver.removeOnGlobalLayoutListener(this)
				val focusedViewHeight = focusedView?.height?.toFloat() ?: 0f
				val offset = focusedViewHeight + triangleSize.toFloat()
				triangleView.x = getTriangleX(showCaseInfoView, triangleSize)
				val showCaseInfoY = calculateVerticalPosition(showCaseInfoView.height.toFloat(), initialPoint, offset)
				val triangleY = calculateVerticalPosition(triangleSize.toFloat(), initialPoint, focusedViewHeight)

				if (animateInfoBubble) {
					// hide from screen because animation starts with a delay
					triangleView.y = -triangleView.height.toFloat()
					showCaseInfoView.y = -showCaseInfoView.height.toFloat()
					animateInfoViews(showCaseInfoView, showCaseInfoY, triangleView, triangleY)
				} else {
					showCaseInfoView.y = showCaseInfoY
					triangleView.y = triangleY
				}
			}
		})
	}

	private fun animateInfoViews(
		showCaseInfoView: View,
		showCaseInfoY: Float,
		triangleView: View,
		triangleY: Float
	) {
		val showCaseInfoStartingY = getAnimationStartY(showCaseInfoY, showCaseInfoView.height)
		val triangleStartingY =
			getAnimationStartY(showCaseInfoY, triangleView.height, showCaseInfoView.height.toFloat())
		val animators = listOf(
			ObjectAnimator.ofFloat(showCaseInfoView, View.Y, showCaseInfoStartingY, showCaseInfoY),
			ObjectAnimator.ofFloat(triangleView, View.Y, triangleStartingY, triangleY)
		)
		AnimatorSet().apply {
			playTogether(animators)
			interpolator = OvershootInterpolator()
			duration = INFORMATION_BUBBLE_ANIM_DURATION
			startDelay = INFORMATION_BUBBLE_ANIM_START_OFFSET
			start()
		}
	}

	private fun getAnimationStartY(showCaseInfoY: Float, viewHeight: Int, offset: Float = 0f) =
		if (showCaseInfoY.isBelowDisplayCenter()) {
			getDisplayMaxY().toFloat() + offset
		} else {
			-(viewHeight.toFloat() + offset)
		}

	private fun getTriangleX(showCaseInfoView: View, triangleSize: Int): Float {
		val cornerRadiusDimen = resources.getDimension(R.dimen.showcase_view_corner_radius)
		val rect = Rect()
		showCaseInfoView.getGlobalVisibleRect(rect)
		val centerXFloat = centerX.toFloat()
		return when {
			centerXFloat.isOnRightSideOfDisplay() && centerXFloat > rect.right - (triangleSize + cornerRadiusDimen) ->
				rect.right - (triangleSize + cornerRadiusDimen)
			centerXFloat.isOnRightSideOfDisplay().not() && centerXFloat < showCaseInfoView.x + cornerRadiusDimen ->
				showCaseInfoView.x + cornerRadiusDimen
			else -> centerX.toFloat() - (triangleSize.toFloat() / 2)
		}
	}

	private fun calculateVerticalPosition(viewHeight: Float, initialPoint: Float, offset: Float): Float {
		val bubbleArrowMargin = if (!hasCircle) resources.getDimension(R.dimen.margin_16dp).toInt() else 0
		return if (initialPoint.isBelowDisplayCenter()) {
			initialPoint - (offset + viewHeight) + bubbleArrowMargin
		} else {
			initialPoint + offset - bubbleArrowMargin
		}
	}

	private fun Float.isBelowDisplayCenter() = (getDisplayMaxY().toFloat() / 2) < this

	private fun Float.isOnRightSideOfDisplay() = (getDisplayMaxX().toFloat() / 2) < this

	private fun getDisplayMaxY() = getDisplaySize().y

	private fun getDisplayMaxX() = getDisplaySize().x

	private fun getDisplaySize() = Point().also { activity.windowManager.defaultDisplay.getSize(it) }

	/**
	 * Circular reveal enter animation
	 */
	private fun doCircularEnterAnimation() {
		viewTreeObserver.addOnGlobalLayoutListener(
			object : ViewTreeObserver.OnGlobalLayoutListener {
				override fun onGlobalLayout() {

					viewTreeObserver.removeOnGlobalLayoutListener(this)

					val revealRadius = hypot(width.toDouble(), height.toDouble()).toInt()
					var startRadius = 0
					if (focusedView != null) {
						startRadius = focusedView!!.width / 2
					} else if (focusCircleRadius > 0 || focusRectangleWidth > 0 || focusRectangleHeight > 0) {
						centerX = focusPositionX
						centerY = focusPositionY
					}
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
						ViewAnimationUtils.createCircularReveal(
							this@ShowCaseView,
							centerX,
							centerY,
							startRadius.toFloat(),
							revealRadius.toFloat()
						).apply {

							duration = animationDuration.toLong()
							addListener(object : AnimatorListenerAdapter() {
								override fun onAnimationEnd(animation: Animator) {}
							})
							interpolator = AnimationUtils.loadInterpolator(
								activity,
								android.R.interpolator.accelerate_cubic
							)
							start()
						}
					}
				}
			})
	}

	/**
	 * Circular reveal exit animation
	 */
	private fun doCircularExitAnimation() {
		if (!isAttachedToWindow) return
		val revealRadius = hypot(width.toDouble(), height.toDouble()).toInt()
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			ViewAnimationUtils.createCircularReveal(
				this,
				centerX,
				centerY,
				revealRadius.toFloat(),
				0f
			).apply {
				duration = animationDuration.toLong()
				interpolator = AnimationUtils.loadInterpolator(
					activity,
					android.R.interpolator.decelerate_cubic
				)
				addListener(object : AnimatorListenerAdapter() {
					override fun onAnimationEnd(animation: Animator) {
						removeView()
					}
				})
				start()
			}
		}
	}

	/**
	 * Removes ShowCaseView view from activity root view
	 */
	fun removeView() {
		stateListener?.onHide()
		if (showCaseImageView != null) showCaseImageView = null
		root?.removeView(this)
	}

	override fun onGlobalLayout() {
		focusedView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
		focus()
	}

	private fun shouldShowCircularAnimation(): Boolean = hasCircularEnterExitAnim

	/**
	 * Builder class for [ShowCaseView]
	 */
	class Builder(private val activity: Activity) {

		private var hasCircle: Boolean = false
		private var customView: View? = null
		private var focusedView: View? = null
		private var clickableView: View? = null
		private var focusCircleRadiusFactor = 1.0
		private var mBackgroundColor: Int = 0
		private var mCloseOnTouch = true
		private var mEnableTouchOnFocusedView: Boolean = false
		private var fitSystemWindows: Boolean = false
		private var mFocusPositionX: Int = 0
		private var mFocusPositionY: Int = 0
		private var mFocusCircleRadius: Int = 0
		private var mFocusRectangleWidth: Int = 0
		private var mFocusRectangleHeight: Int = 0
		private var focusAnimationEnabled = true
		private var mFocusAnimationMaxValue = FOCUS_ANIM_MAX
		private var mFocusAnimationStep = 1
		private var animateInfoBubble = false
		private var delay: Long = 0
		private var hasCircularEnterExitAnm = false
		private var showCaseBubbleModel: ShowCaseBubbleModel? = null
		private var showCaseBubbleListener: ShowCaseBubbleListener? = null
		private var stateListener: OnShowCaseStateListener? = null
		private var highlightedAreaClickListener: HighlightedAreaClickListener? = null

		/**
		 * @param view view to focus
		 * @return Builder
		 */
		fun clickableOn(view: View): Builder {
			clickableView = view
			return this
		}

		/**
		 * @param view view to focus
		 * @return Builder
		 */
		fun focusOn(view: View): Builder {
			focusedView = view
			return this
		}

		fun hasCircle(hasCircle: Boolean): Builder {
			this.hasCircle = hasCircle
			return this
		}

		fun setCustomView(view: View): Builder {
			customView = view
			return this
		}

		/**
		 * @param backgroundColor background color of ShowCaseView
		 * @return Builder
		 */
		fun backgroundColor(backgroundColor: Int): Builder {
			mBackgroundColor = backgroundColor
			return this
		}

		/**
		 * @param factor focus circle radius factor (default value = 1)
		 * @return Builder
		 */
		fun focusCircleRadiusFactor(factor: Double): Builder {
			focusCircleRadiusFactor = factor
			return this
		}

		/**
		 * @param closeOnTouch closes on touch if enabled
		 * @return Builder
		 */
		fun closeOnTouch(closeOnTouch: Boolean): Builder {
			mCloseOnTouch = closeOnTouch
			return this
		}

		/**
		 * @param enableTouchOnFocusedView closes on touch of focused view if enabled
		 * @return Builder
		 */
		fun enableTouchOnFocusedView(enableTouchOnFocusedView: Boolean): Builder {
			mEnableTouchOnFocusedView = enableTouchOnFocusedView
			return this
		}

		/**
		 * This should be the same as root view's fitSystemWindows value
		 *
		 * @param fitSystemWindows fitSystemWindows value
		 * @return Builder
		 */
		fun fitSystemWindows(fitSystemWindows: Boolean): Builder {
			this.fitSystemWindows = fitSystemWindows
			return this
		}

		/**
		 * disable Focus Animation
		 *
		 * @return Builder
		 */
		fun disableFocusAnimation(): Builder {
			focusAnimationEnabled = false
			return this
		}

		fun focusAnimationMaxValue(focusAnimationMaxValue: Int): Builder {
			mFocusAnimationMaxValue = focusAnimationMaxValue
			return this
		}

		fun focusAnimationStep(focusAnimationStep: Int): Builder {
			mFocusAnimationStep = focusAnimationStep
			return this
		}

		fun animateInfoBubble(animateInfoBubble: Boolean): Builder {
			this.animateInfoBubble = animateInfoBubble
			return this
		}

		fun delay(delayInMillis: Int): Builder {
			delay = delayInMillis.toLong()
			return this
		}

		/**
		 * Uses circular enter exit animation for the background
		 */
		fun hasCircularAnim(hasCircularEnterExitAnim: Boolean): Builder {
			hasCircularEnterExitAnm = hasCircularEnterExitAnim
			return this
		}

		fun setShowCaseBubbleViewModel(showCaseBubbleModel: ShowCaseBubbleModel): Builder {
			this.showCaseBubbleModel = showCaseBubbleModel
			return this
		}

		fun setShowCaseBubbleListener(showCaseBubbleListener: ShowCaseBubbleListener): Builder {
			this.showCaseBubbleListener = showCaseBubbleListener
			return this
		}

		fun setStateListener(stateListener: OnShowCaseStateListener): Builder {
			this.stateListener = stateListener
			return this
		}

		fun setHighlightedAreaClickListener(highlightedAreaClickListener: HighlightedAreaClickListener): Builder {
			this.highlightedAreaClickListener = highlightedAreaClickListener
			return this
		}

		/**
		 * builds the builder
		 *
		 * @return [ShowCaseView] with given parameters
		 */
		fun build(): ShowCaseView {
			return ShowCaseView(
				hasCircle = hasCircle,
				customView = customView,
				activity = activity,
				focusView = focusedView,
				clickableView = clickableView,
				focusCircleRadiusFactor = focusCircleRadiusFactor,
				backgroundColor = mBackgroundColor,
				closeOnTouch = mCloseOnTouch,
				enableTouchOnFocusedView = mEnableTouchOnFocusedView,
				fitSystemWindows = fitSystemWindows,
				focusPositionX = mFocusPositionX,
				focusPositionY = mFocusPositionY,
				focusCircleRadius = mFocusCircleRadius,
				focusRectangleWidth = mFocusRectangleWidth,
				focusRectangleHeight = mFocusRectangleHeight,
				animationEnabled = focusAnimationEnabled,
				focusAnimationMaxValue = mFocusAnimationMaxValue,
				focusAnimationStep = mFocusAnimationStep,
				animateInfoBubble = animateInfoBubble,
				delay = delay,
				hasCircularAnim = hasCircularEnterExitAnm,
				showCaseBubbleModel = showCaseBubbleModel,
				showCaseBubbleListener = showCaseBubbleListener,
				stateListener = stateListener,
				highlightedAreaClickListener = highlightedAreaClickListener
			)
		}
	}
}

/**
 * A listener for then the this [ShowCaseView] is being shown or hidden.
 */
interface OnShowCaseStateListener {

	/**
	 * Invoked when this [ShowCaseView] is shown.
	 */
	fun onShow()

	/**
	 * Invoked when this [ShowCaseView] is hidden.
	 */
	fun onHide()
}

/**
 * A click listener for when the highlighted area is clicked.
 */
interface HighlightedAreaClickListener {

	/**
	 * Invoked when the highlighted area is clicked.
	 */
	fun onHighlightedAreaClick()
}
