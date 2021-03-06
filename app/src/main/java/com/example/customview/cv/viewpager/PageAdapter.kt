package com.example.customview.cv.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PagerAdapter(
	fragmentManager: FragmentManager,
	private val fragments: List<Fragment>,
	private val tabNamesList: List<String> = emptyList()
) : FragmentStatePagerAdapter(fragmentManager) {

	override fun getItem(position: Int): Fragment = fragments[position]

	override fun getCount(): Int = fragments.size

	override fun getPageTitle(position: Int) = tabNamesList.getOrNull(position % tabNamesList.size)
}