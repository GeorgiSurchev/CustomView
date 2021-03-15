package com.example.customview.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.customview.R
import com.example.customview.cv.bubbleview.OnShowCaseStateListener
import com.example.customview.cv.bubbleview.ShowCaseView
import com.example.customview.cv.currencyconverter.Currency
import com.example.customview.cv.currencyconverter.ICurrencyConverterListener
import com.example.customview.cv.spinner.SpinarAdapter
import com.example.customview.databinding.MainFragmentBinding

class MainFragment : Fragment() {

	private lateinit var binding: MainFragmentBinding
	private lateinit var viewModel: MainViewModel
	private var finalCurrencyList = mutableListOf<Currency>()
	private var colorList = listOf<Int>()
	private var currentColor = 0
	private var convertToEU = false
	private var bubbleView: ShowCaseView? = null

	private val animateRunnable = Runnable {
		binding.mainCurrencyConverter.setAllFieldsVisibilityExceptInput(View.VISIBLE)
	}

	private var showCaseStateListener = object : OnShowCaseStateListener {
		override fun onShow() {
			//leave empty
		}

		override fun onHide() {
			bubbleView = buildBubbleDialogView()
			bubbleView?.show()
		}
	}

	private var dialogViewCaseStateListener = object : OnShowCaseStateListener {
		override fun onShow() {
			//leave empty
		}

		override fun onHide() {
			setShowSecondCaseBubbleView()
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
		binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
		binding.lifecycleOwner = this
		binding.mainViewModel = viewModel
		colorList = requireContext().resources.getIntArray(R.array.colors).toList()
		observeSpinnerInput()
		setShowCaseBubbleView()
		binding.mainCurrencyConverter.apply {
			setAllFieldsVisibilityExceptInput(View.INVISIBLE)
			postDelayed(animateRunnable,1500)
		}
		currentColor = ContextCompat.getColor(requireContext(), R.color.black)
		setFinalListOfCurrency()
		setCurrencySpinnerAdapter()
		currencyConverterListener(finalCurrencyList)

		return binding.root
	}

	private fun observeSpinnerInput() {
		viewModel.spinnerInputText.observe(viewLifecycleOwner, Observer {
			it?.let { spinnerInputText ->
				shouldUpdateAllFields(spinnerInputText)
			}
		})
	}


	private fun setShowCaseBubbleView() {
		val showCaseBubbleViewModel = viewModel.getShowCaseBubbleViewModel()
		bubbleView = ShowCaseView.Builder(requireActivity())
			.focusOn(binding.mainCurrencyConverter.binding.currencyConverterInputNumber)
			.hasCircle(true)
			.animateInfoBubble(true)
			.setShowCaseBubbleViewModel(showCaseBubbleViewModel)
			.closeOnTouch(false)
			.delay(3000)
			.hasCircularAnim(false)
			.focusCircleRadiusFactor(0.80)
			.enableTouchOnFocusedView(true)
			.clickableOn(binding.mainCurrencyConverter.binding.currencyConverterInputNumber)
			.setStateListener(showCaseStateListener)
			.build()
		bubbleView?.show()
	}

	private fun setShowSecondCaseBubbleView() {
		val showCaseBubbleViewModel = viewModel.getShowSecondCaseBubbleViewModel()
		bubbleView = ShowCaseView.Builder(requireActivity())
			.focusOn(binding.mainCurrencyConverter.binding.currencyConverterBgnButton)
			.hasCircle(true)
			.disableFocusAnimation()
			.animateInfoBubble(false)
			.setShowCaseBubbleViewModel(showCaseBubbleViewModel)
			.closeOnTouch(false)
			.delay(1500)
			.enableTouchOnFocusedView(false)
			.focusCircleRadiusFactor(2.5)
			.backgroundColor(ResourcesCompat.getColor(resources, R.color.Blue, null))
			.build()
		bubbleView?.show()
	}

	private fun buildBubbleDialogView(): ShowCaseView = ShowCaseView.Builder(requireActivity())
		.setCustomView(inflateTooltipBubbleView())
		.focusOn(binding.mainCurrencyConverter.binding.currencyConverterPrefix)
		.closeOnTouch(true)
		.clickableOn(binding.mainCurrencyConverter.binding.currencyConverterPrefix)
		.setStateListener(dialogViewCaseStateListener)
		.build()

	private fun inflateTooltipBubbleView(): View {
		val tooltipBubbleView = layoutInflater.inflate(R.layout.show_case_tooltip_bubble, null)
		val tooltipBubbleText = tooltipBubbleView.findViewById(R.id.show_case_tooltip_bubble_tv) as TextView
		tooltipBubbleText.text = DIALOG_BUBBLE_VIEW_BODY
		tooltipBubbleView.layoutParams = FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.WRAP_CONTENT
		).apply {
			val startEndMargin = resources.getDimension(R.dimen.showcase_view_bubble_layout_margin_start_end).toInt()
			setMargins(startEndMargin, 0, startEndMargin, 0)
		}
		return tooltipBubbleView
	}

	private fun setFinalListOfCurrency() {
		val setFinalListOfCurrency = java.util.Currency.getAvailableCurrencies()
		val listOfCurrencyPerUnitInLeva = viewModel.listOfCurrencyPerUnitInLeva()
		val currencyNameList = requireContext().resources.getStringArray(R.array.country_code_list).toList()
		setFinalListOfCurrency.forEach { item ->
			currencyNameList.forEachIndexed { index, element ->
				if (item.currencyCode == element) {
					val currency = Currency(
						name = item.displayName,
						symbol = item.symbol,
						bgn = listOfCurrencyPerUnitInLeva[index]
					)
					finalCurrencyList.add(currency)
				}
			}
		}
	}

	private fun setCurrencySpinnerAdapter() {
		val listOfCurrencyName = finalCurrencyList.map { it.name }
		context?.let { context ->
			val adapter = SpinarAdapter(
				context = context,
				resource = R.layout.spinner_item,
				items = listOfCurrencyName
			)
			binding.mainCurrencySpinner.setAdapter(adapter)
		}
	}

	private fun currencyConverterListener(
		finalCurrencyList: MutableList<Currency>
	) {
		binding.mainCurrencyConverter.setListener(object : ICurrencyConverterListener {
			override fun onPrefixClicked() {
				val currency = finalCurrencyList.random()
				currentColor = colorList.random()

				val isInputNumberNotEmpty = binding.mainCurrencyConverter.isInputNumberNotEmpty()
				if (isInputNumberNotEmpty) {
					viewModel.spinnerInputText.value = currency.name
					viewModel.spinnerTextColor.value = currentColor
				}
				clearCurrencyConverterFocusAndHideKeyboard()
			}

			override fun onInputNumberClicked() {
				binding.mainCurrencyConverter.changeTextColor(colorList.random())
			}

			override fun onBGNButtonClicked() {
				convertToEU = false
				shouldUpdateAllFields(viewModel.spinnerInputText.value.orEmpty())
			}

			override fun onEuroButtonClicked() {
				convertToEU = true
				shouldUpdateAllFields(viewModel.spinnerInputText.value.orEmpty())
			}
		})
	}

	private fun shouldUpdateAllFields(name: String) {
		val currency = finalCurrencyList.firstOrNull { currency ->
			currency.name == name
		}
		if (currency != null) {
			binding.mainCurrencyConverter.updateFields(
				color = currentColor,
				currency = currency,
				convertToEU = convertToEU
			)
			clearCurrencyConverterFocusAndHideKeyboard()
		}
	}

	private fun clearCurrencyConverterFocusAndHideKeyboard() {
		binding.mainCurrencyConverter.clearFocus()
		binding.mainCurrencySpinner.clearFocus()
		val imm: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.hideSoftInputFromWindow(binding.mainCurrencyConverter.windowToken, 0)
	}
}
