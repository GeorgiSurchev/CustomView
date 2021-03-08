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
import com.example.customview.cv.viewpager.FlipPageTransformer
import com.example.customview.cv.viewpager.PagerAdapter
import com.example.customview.cv.viewpager.ViewPagerCustomDuration
import com.example.customview.databinding.MainFragmentBinding
import com.example.customview.ui.main.pagefragments.FirestPageFragment
import com.example.customview.ui.main.pagefragments.SecondPageFragment
import com.google.android.material.tabs.TabLayout

private const val FLIP_PAGE_DURATION_MILLIS = 875

class MainFragment : Fragment() {

	private lateinit var binding: MainFragmentBinding
	private lateinit var viewModel: MainViewModel
	private lateinit var datapter: PagerAdapter
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
		setTabLayout()
		currentColor = ContextCompat.getColor(requireContext(), R.color.black)
		setFinalListOfCurrency()
		setCurencySpinnerAdapter()
		initPager(binding.mainCustomViewPager)
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
		val listOfCurrencyPerUnitInLeva = listOfCurrencyPerUnitInLeva()
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
			.focusOn(binding.mainCurrencyConverter)
			.hasCircle(true)
			.animateInfoBubble(true)
			.setShowCaseBubbleViewModel(showCaseBubbleViewModel)
			.closeOnTouch(false)
			.hasCircularAnim(false)
			.enableTouchOnFocusedView(true)
			.clickableOn(binding.mainCurrencyConverter)
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
						viewModel.spinnerInputText.value = "US Dollar"
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

	private fun setTabLayout() {
		binding.mainTabLayout.setupWithViewPager(binding.mainCustomViewPager)
		binding.mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
			override fun onTabReselected(p0: TabLayout.Tab?) {
				//empty
			}

			override fun onTabUnselected(p0: TabLayout.Tab?) {
				//empty
			}

			override fun onTabSelected(p0: TabLayout.Tab?) {
				pageIndex = p0?.position ?: 0
			}
		})
	}

	private fun initPager(pager: ViewPagerCustomDuration) {
		val firstPageFragment = FirestPageFragment.newInstance { onPageButtonClick() }
		val secondPageFragment = SecondPageFragment.newInstance { onPageButtonClick() }
		val tabNames = viewModel.getTabNames()
		datapter = PagerAdapter(childFragmentManager, listOf(firstPageFragment, secondPageFragment), tabNames)
		pager.adapter = datapter
		pager.setPageTransformer(false, FlipPageTransformer())
		pager.setScrollDuration(FLIP_PAGE_DURATION_MILLIS)
		selectTab()
	}

	private fun onPageButtonClick() {
		viewModel.spinnerInputText.value?.let {
			shouldUpdateAllFields(it)
		}
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

	private fun selectTab() {
		val selectPosition = 0
		binding.mainTabLayout.getTabAt(selectPosition)?.select()
	}

	private fun setCurencySpinnerAdapter() {
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

	private fun listOfCurrencyPerUnitInLeva() = listOf(1.26821, 0.285994, 1.28462, 1.76998, 0.251263, 0.0748156,
		0.263018, 2.26283, 0.209635, 0.25794, 0.00537759, 0.492652, 0.0221601, 0.0128588, 0.015211, 0.00144409, 0.0786194, 0.400752,
		0.19071, 1.1807, 0.0335258, 0.431541, 0.401097, 0.0220528, 0.192883, 1.22117, 0.0537301, 0.221873, 1.62606, 0.107847
	)

	data class Currency(
		val name: String = "",
		val symbol: String = "",
		val bgn: Double = 0.00
	)
}