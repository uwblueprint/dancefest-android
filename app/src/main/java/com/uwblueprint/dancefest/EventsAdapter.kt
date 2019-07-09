package com.uwblueprint.dancefest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uwblueprint.dancefest.models.Event
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventsAdapter(private val listener: EventItemListener, private val events: ArrayList<Event>) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    companion object {
        private const val USER_DATE_PATTERN = "MMM dd, yyyy"
        private val dateFormat = SimpleDateFormat(USER_DATE_PATTERN, Locale.CANADA)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.name_text
        var dateView: TextView = view.date_text
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        holder.nameView.text = event.eventTitle
        if (event.eventDate != null) {
            holder.dateView.text = dateFormat.format(event.eventDate)
        }

        holder.view.setOnClickListener { listener.onItemClicked(event) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(itemView)
    }
}
