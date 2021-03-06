package com.example.customview.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.customview.cv.bubbleview.ShowCaseView
import com.example.customview.R
import com.example.customview.cv.currencyconverter.ICurrencyConverter
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
	private var currencyList = listOf<Currency>()
	var colorList = listOf<Int>()
	var pageIndex = 0
	val convertToEU = pageIndex != 0

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
		binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
		binding.lifecycleOwner = this
		binding.mainViewModel = viewModel
		currencyList = getListOfCurrency()
		colorList = requireContext().resources.getIntArray(R.array.colors).toList()
		observeSpinnerInput()
		setShowCaseBubbleView()
		setTabLayout()

		val allCurrency = java.util.Currency.getAvailableCurrencies()
		val finalCurrencyList = mutableListOf<Currency>()

		for (item in allCurrency) {
			for (items in currencyList) {
				if (item.currencyCode == items.symbol) {
					finalCurrencyList.add(items.copy(symbol = item.symbol))
				}
			}
		}
		val listOfCurrencyName = finalCurrencyList.map { it.name }
		setPostalCodeSpinnerAdapter(listOfCurrencyName)
		initPager(binding.mainCustomViewPager)
		currencyConverterListener(finalCurrencyList)

		return binding.root
	}

	private fun observeSpinnerInput() {
		viewModel.spinnerInputText.observe(viewLifecycleOwner, Observer {
			it?.let { name ->
				val names = currencyList.firstOrNull { currency ->
					currency.name == name
				}
				if (names != null) {
					val shouldUpdateFields = binding.mainCurrencyConverter.shouldUpdateFields(
						colorList.random(),
						names,
						convertToEU
					)
					if (shouldUpdateFields) {
						Toast.makeText(context, names.name, Toast.LENGTH_SHORT).show()
					}
				}
			}
		})
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
		binding.mainCurrencyConverter.setListener(object : ICurrencyConverter {
			override fun onPrefixClicked() {
				val currency = finalCurrencyList.random()
				val color = colorList.random()
				viewModel.spinnerInputText.value = currency.name
				viewModel.spinnerTextColor.value = color

				val shouldUpdateFields = binding.mainCurrencyConverter.shouldUpdateFields(color, currency, convertToEU)
				if (shouldUpdateFields) {
					Toast.makeText(context, currency.name, Toast.LENGTH_SHORT).show()
				}
				val imm: InputMethodManager =
					context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(binding.mainCurrencyConverter.windowToken, 0)
				binding.mainCurrencyConverter.clearFocus()
				binding.mainCurencySpinner.clearFocus()
			}

			override fun onInputNumberClicked() {
				binding.mainCurrencyConverter.changeTextColor(colorList.random())
			}
		})
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
		val firstPageFragment = FirestPageFragment.newInstance { onPageClick() }
		val secondPageFragment = SecondPageFragment.newInstance { onPageClick() }
		val tabNames = viewModel.getTabNames()
		datapter = PagerAdapter(childFragmentManager, listOf(firstPageFragment, secondPageFragment), tabNames)
		pager.adapter = datapter
		pager.setPageTransformer(false, FlipPageTransformer())
		pager.setScrollDuration(FLIP_PAGE_DURATION_MILLIS)
		selectTab()
	}

	private fun onPageClick() {
		viewModel.spinnerInputText.value?.let { name ->
			val names = currencyList.firstOrNull { currency ->
				currency.name == name
			}
			if (names != null) {
				val shouldUpdateFields = binding.mainCurrencyConverter.shouldUpdateFields(
					colorList.random(),
					names,
					convertToEU
				)
				if (shouldUpdateFields) {
					Toast.makeText(context, names.name, Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun selectTab() {
		val selectPosition = 0
		binding.mainTabLayout.getTabAt(selectPosition)?.select()
	}

	private fun setPostalCodeSpinnerAdapter(postCodeAndCityList: List<String>) {
		context?.let { context ->
			val adapter = SpinarAdapter(
				context,
				R.layout.spinner_item, postCodeAndCityList
			)
			binding.mainCurencySpinner.setAdapter(adapter)
		}
	}

	private fun getListOfCurrency() = listOf(
		Currency("Australian dollar", "AUD", 1.26821),
		Currency("Brazilian Real", "BRL", 0.285994),
		Currency("Canadian dollar", "CAD", 1.28462),
		Currency("Swiss franc", "CHF", 1.76998),
		Currency("Chinese renminbi yuan", "CNY", 0.251263),
		Currency("Czech koruna", "CZK", 0.0748156),
		Currency("Danish krone", "DKK", 0.263018),
		Currency("British pound", "GBP", 2.26283),
		Currency("Hong Kong Dollar", "HKD", 0.209635),
		Currency("Croatian Kuna", "HRK", 0.25794),
		Currency("Hungarian forint", "HUF", 0.00537759),
		Currency("Indonesian Rupiah", "IDR", 0.000113512),
		Currency("Israeli shekel", "ILS", 0.492652),
		Currency("Indian rupee", "INR", 0.0221601),
		Currency("Icelandic krona", "ISK", 0.0128588),
		Currency("Japanese yen", "JPY", 0.015211),
		Currency("South Korean won", "KRW", 0.00144409),
		Currency("Mexican peso", "MXN", 0.0786194),
		Currency("Malaysian ringitis", "MYR", 0.400752),
		Currency("Norwegian krone", "NOK", 0.19071),
		Currency("New Zealand Dollar", "NZD", 1.1807),
		Currency("Philippine Peso", "PHP", 0.0335258),
		Currency("Polish zloty", "PLN", 0.431541),
		Currency("Romanian leu", "RON", 0.401097),
		Currency("Russian ruble", "RUB", 0.0220528),
		Currency("Swedish krona", "SEK", 0.192883),
		Currency("Singapore dollar", "SGD", 1.22117),
		Currency("Thai baht", "THB", 0.0537301),
		Currency("Turkish lira", "TRY", 0.221873),
		Currency("US dollar", "USD", 1.62606),
		Currency("South African Rand", "ZAR", 0.107847)
	)

	data class Currency(
		val name: String = "",
		val symbol: String = "",
		val bgn: Double = 0.00
	)
}