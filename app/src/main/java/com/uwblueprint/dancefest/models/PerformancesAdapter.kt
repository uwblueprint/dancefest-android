package com.uwblueprint.dancefest.models

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uwblueprint.dancefest.R
import kotlinx.android.synthetic.main.item_performance.view.*

class PerformancesAdapter(
    private val activity: Activity,
    private val adjudications: HashMap<*, *>,
    private val performances: ArrayList<Performance>
) : RecyclerView.Adapter<PerformancesAdapter.ViewHolder>() {

    companion object {
        const val TAG_PERFORMANCE = "TAG_PERFORMANCE"
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.name_performance
    }

    override fun getItemCount() = performances.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performance = performances[position]
        holder.nameView.text = performance.danceTitle
        holder.view.setOnClickListener {
            // TODO: Go to adjudications page.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performance, parent, false)
        return ViewHolder(itemView)
    }
}