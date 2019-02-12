package com.uwblueprint.dancefest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.item_performance.view.*

class PerformancesAdapter(
    private val adjudications: HashMap<*, *>,
    private val listener: PerformanceItemListener,
    private val performances: ArrayList<Performance>
) : RecyclerView.Adapter<PerformancesAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.name_performance
    }

    override fun getItemCount() = performances.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performance = performances[position]
        val (_, _, _, _, _, danceTitle, performanceId) = performance
        holder.nameView.text = danceTitle
        holder.view.setOnClickListener {
            val adjudication = adjudications[performanceId] as? Adjudication
            listener.onItemClicked(adjudication, performance)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performance, parent, false)
        return ViewHolder(itemView)
    }
}