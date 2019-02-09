package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.fragment_performance.view.*

class PerformanceFragment : Fragment() {

    private lateinit var adjudications: HashMap<*, *>
    private lateinit var performances: ArrayList<Performance>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_performance, container, false)
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_ADJUDICATIONS) }?.apply {
            val newAdjudications = getSerializable(PerformanceActivity.TAG_ADJUDICATIONS)
            adjudications = if (newAdjudications is HashMap<*, *>) {
                newAdjudications
            } else {
                hashMapOf<String, Adjudication>()
            }
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_PERFORMANCES) }?.apply {
            performances = getParcelableArrayList(PerformanceActivity.TAG_PERFORMANCES)
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_TITLE) }?.apply {
            rootView.title_performances.text = getString(PerformanceActivity.TAG_TITLE)
        }
        return rootView
    }
}