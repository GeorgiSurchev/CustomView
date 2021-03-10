package com.example.customview.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.customview.R
import com.example.customview.cv.bubbleview.ShowCaseView
import com.example.customview.cv.currencyconverter.ICurrencyConverterListener
import com.example.customview.cv.spinner.SpinarAdapter
import com.example.customview.databinding.MainFragmentBinding

class MainFragment : Fragment() {

	private lateinit var binding: MainFragmentBinding
	private lateinit var viewModel: MainViewModel
	private var finalCurrencyList = mutableListOf<Currency>()
	private var colorList = listOf<Int>()
	private var currentColor = 0
	private var pageIndex = 0
	private val convertToEU
		get() = pageIndex != 0
	private var isFirstCurrencyClick = true

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

	private fun setFinalListOfCurrency() {
		val setFinalListOfCurrency = java.util.Currency.getAvailableCurrencies()
		val listOfCurrencyPerUnitInLeva = viewModel.listOfCurrencyPerUnitInLeva()
		val currencyNameList = requireContext().resources.getStringArray(R.array.country_code_list).toList()
		setFinalListOfCurrency.forEach { item ->
			currencyNameList.forEachIndexed { index, element ->
				if (item.currencyCode == element) {
					val currency =
						Currency(name = item.displayName, symbol = item.symbol, listOfCurrencyPerUnitInLeva[index])
					finalCurrencyList.add(currency)
				}
			}
		}
	}

	private fun setShowCaseBubbleView() {
		val showCaseBubbleViewModel = viewModel.getShowCaseBubbleViewModel()
		val showCaseView = ShowCaseView.Builder(requireActivity())
			.focusOn(binding.mainCurrencyConverter.binding.currencyConverterInputNumber)
			.hasCircle(true)
			.animateInfoBubble(true)
			.setShowCaseBubbleViewModel(showCaseBubbleViewModel)
			.closeOnTouch(false)
			.hasCircularAnim(false)
			.enableTouchOnFocusedView(true)
			.clickableOn(binding.mainCurrencyConverter.binding.currencyConverterInputNumber)
			.build()
		showCaseView.show()
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
					if (isFirstCurrencyClick) {
						viewModel.spinnerInputText.value = DEFAULT_SPINNER_INPUT_TEXT
						isFirstCurrencyClick = false
						clearCurrencyConverterFocus()
						return
					}
					viewModel.spinnerInputText.value = currency.name
					viewModel.spinnerTextColor.value = currentColor
				}
				val imm: InputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(binding.mainCurrencyConverter.windowToken, 0)
				clearCurrencyConverterFocus()
			}

			override fun onInputNumberClicked() {
				binding.mainCurrencyConverter.changeTextColor(colorList.random())
			}
		})
	}

	fun clearCurrencyConverterFocus() {
		binding.mainCurrencyConverter.clearFocus()
		binding.mainCurencySpinner.clearFocus()
	}

	private fun shouldUpdateAllFields(name: String) {
		val names = finalCurrencyList.firstOrNull { currency ->
			currency.name == name
		}
		if (names != null) {
			binding.mainCurrencyConverter.updateFields(
				currentColor,
				names,
				convertToEU
			)
			clearCurrencyConverterFocus()
		}
	}

	private fun setCurrencySpinnerAdapter() {
		val listOfCurrencyName = finalCurrencyList.map { it.name }
		context?.let { context ->
			val adapter = SpinarAdapter(
				context,
				R.layout.spinner_item,
				listOfCurrencyName
			)
			binding.mainCurencySpinner.setAdapter(adapter)
		}
	}

	data class Currency(
		val name: String = "",
		val symbol: String = "",
		val bgn: Double = 0.00
	)
}