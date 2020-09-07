package com.uwblueprint.dancefest

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uwblueprint.dancefest.models.Event
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.DateFormat
import java.util.*

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private var events: ArrayList<Event> = arrayListOf()

    companion object {
        private val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.CANADA)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.bindEvent(event)
    }

    override fun getItemCount() = events.size

    fun updateEvents(events: ArrayList<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var nameView: TextView = v.name_text
        private var dateView: TextView = v.date_text
        private var event: Event? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindEvent(e: Event) {
            event = e
            nameView.text = e.eventTitle
            if (e.eventDate != null) {
                dateView.text = dateFormat.format(e.eventDate)
            }
        }

        override fun onClick(v: View) {
            val context = v.context
            val intent = Intent(context, PerformanceActivity::class.java)
            intent.putExtra(EventActivity.TAG_EVENT, event)
            context.startActivity(intent)
        }
    }
}
