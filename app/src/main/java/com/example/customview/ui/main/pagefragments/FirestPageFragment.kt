package com.example.customview.ui.main.pagefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.customview.R
import com.example.customview.databinding.FragmentFirstPageBinding

class FirestPageFragment : Fragment() {

	var listener: (() -> Unit)? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding: FragmentFirstPageBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_first_page, container, false)
		binding.bubble.setOnClickListener {
			listener?.invoke()
		}

		return binding.root
	}

	companion object {

		fun newInstance(
			listener: (() -> Unit)? = null
		): FirestPageFragment = FirestPageFragment().apply {
			this.listener = listener
		}
	}
}
