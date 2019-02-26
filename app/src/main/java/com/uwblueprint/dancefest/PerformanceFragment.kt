package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance
import kotlinx.android.synthetic.main.fragment_performance.*
import kotlinx.android.synthetic.main.fragment_performance.view.*

class PerformanceFragment : Fragment(), PerformanceItemListener {

    private lateinit var adjudications: HashMap<*, *>
    private lateinit var performances: ArrayList<Performance>
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

    override fun onItemClicked(adjudication: Adjudication?, performance: Performance) {
        // TODO: Go to Adjudications page.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(context)
        viewAdapter = PerformancesAdapter(adjudications, this, performances)
        list_performances.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}
