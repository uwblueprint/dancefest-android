package com.uwblueprint.dancefest

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.PAPair
import kotlinx.android.synthetic.main.item_performance.view.*

class PerformancesAdapter(
        private val tabletId: Int,
        private val event: Event
) : RecyclerView.Adapter<PerformancesAdapter.ViewHolder>() {

    private var paPairs: ArrayList<PAPair> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_performance, parent, false)
        return ViewHolder(itemView, tabletId, event)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val performance = paPairs[position]
        holder.bindPerformance(performance)
    }

    override fun getItemCount() = paPairs.size

    fun updatePerformances(ps: List<PAPair>) {
        paPairs.clear()
        paPairs.addAll(ps)
        notifyDataSetChanged()
    }

    class ViewHolder(
            private val view: View,
            private val tabletId: Int,
            private val event: Event)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val danceEntryView: TextView = view.dance_entry_text
        private val nameView: TextView = view.name_performance
        private var paPair: PAPair? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindPerformance(p: PAPair) {
            paPair = p
            danceEntryView.text = paPair!!.performance.danceEntry.toString()
            nameView.text = paPair!!.performance.danceTitle
        }

        override fun onClick(v: View) {
            val context = v.context
            val performance = paPair?.performance
            val adjudication = paPair?.adjudication
            val intent = Intent(context, CritiqueFormActivity::class.java)
            if (adjudication != null) {
                intent.putExtra(PerformanceListFragment.TAG_ADJUDICATIONS, adjudication)
            }
            intent.putExtra(PerformanceActivity.TAG_EVENT_ID, event.eventId)
            intent.putExtra(PerformanceActivity.TAG_TITLE, event.eventTitle)
            intent.putExtra(PerformanceListFragment.TAG_PERFORMANCES, performance as Parcelable)
            intent.putExtra(PerformanceActivity.TAG_TABLET_ID, tabletId)
            context.startActivity(intent)
        }
    }
}
