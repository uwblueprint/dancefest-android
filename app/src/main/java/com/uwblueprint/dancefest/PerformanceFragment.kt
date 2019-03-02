package com.uwblueprint.dancefest

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
    private var eventID: String? = null
    private var eventTitle: String? = null
    private lateinit var performances: ArrayList<Performance>
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object {
        const val TAG_ADJUDICATION = "TAG_ADJUDICATION"
        const val TAG_PERFORMANCE = "TAG_PERFORMANCE"
    }

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
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_EVENT_ID) }?.apply {
            eventID = getString(PerformanceActivity.TAG_EVENT_ID)
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_PERFORMANCES) }?.apply {
            performances = getParcelableArrayList(PerformanceActivity.TAG_PERFORMANCES)
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_TITLE) }?.apply {
            val title = getString(PerformanceActivity.TAG_TITLE)
            eventTitle = title
            rootView.title_performances.text = title
        }

        return rootView
    }

    override fun onItemClicked(adjudication: Adjudication?, performance: Performance) {
        val intent = Intent(context, CritiqueFormActivity::class.java)
        if (adjudication != null) {
            intent.putExtra(TAG_ADJUDICATION, adjudication)
        }
        intent.putExtra(PerformanceActivity.TAG_EVENT_ID, eventID)
        intent.putExtra(PerformanceActivity.TAG_TITLE, eventTitle)
        intent.putExtra(TAG_PERFORMANCE, performance as Parcelable)
        startActivity(intent)
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
