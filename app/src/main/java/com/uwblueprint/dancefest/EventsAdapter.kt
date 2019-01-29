package com.uwblueprint.dancefest

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.item_event.view.*

class EventsAdapter(private val events: ArrayList<Event>, private val activity: Activity) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    companion object {
        const val TAG_EVENT = "TAG_EVENT"
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.name_text
        var dateView: TextView = view.date_text
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.nameView.text = event.name
        holder.dateView.text = event.date
        holder.view.setOnClickListener {
            val intent = Intent(activity, PerformanceActivity::class.java)
            intent.putExtra(TAG_EVENT, event)
            activity.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(itemView)
    }
}
