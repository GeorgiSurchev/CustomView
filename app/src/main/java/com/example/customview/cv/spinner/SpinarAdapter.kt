package com.example.customview.cv.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView

class SpinarAdapter(
	context: Context,
	private val resource: Int,
	items: List<String>
) : ArrayAdapter<String>(context, resource, items) {

	val tempItems: List<String> = ArrayList(items)
	val suggestions: MutableList<String> = ArrayList()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val view: View = if (convertView == null) {
			val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
			inflater.inflate(resource, parent, false)
		} else {
			convertView
		}
		(view as? TextView)?.text = getItem(position)

		return view
	}

	override fun getFilter(): Filter = nameFilter

	/**
	 * Custom Filter implementation for custom suggestions .
	 */
	private val nameFilter: Filter = object : Filter() {
		override fun convertResultToString(resultValue: Any): CharSequence = resultValue as String

		override fun performFiltering(constraint: CharSequence?): FilterResults {
			return if (constraint != null) {
				suggestions.clear()
				tempItems.filterTo(suggestions) {
					it.contains(constraint, ignoreCase = true)
				}

				FilterResults().apply {
					values = suggestions
					count = suggestions.size
				}
			} else {
				FilterResults()
			}
		}

		override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
			val filterList = results?.values as? ArrayList<String> ?: return

			if (filterList.isNotEmpty()) {
				clear()
				addAll(filterList)
				notifyDataSetChanged()
			}
		}
	}
}