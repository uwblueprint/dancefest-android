package com.uwblueprint.dancefest

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

class AwardsAdapter(private val context: Context)
    : RecyclerView.Adapter<AwardsAdapter.ViewHolder>() {

    private var awards: ArrayList<String> = arrayListOf()
    private var selectedAwards: MutableMap<String, Boolean> = mutableMapOf()

    init {
        awards.forEach { selectedAwards.putIfAbsent(it, false) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_award, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.e("YEET", "" + position)
        val award = awards[position]
        viewHolder.bind(award)
        viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectedAwards[award] = isChecked
        }
    }

    override fun getItemCount(): Int = awards.size

    fun updateAwards(aws: List<String>) {
        awards.addAll(aws)
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var checkBox: CheckBox = v.findViewById(R.id.award_check_box)
        var awardLabel: TextView = v.findViewById(R.id.award_label)

        fun bind(award: String) {
            awardLabel.text = award
        }
    }
}