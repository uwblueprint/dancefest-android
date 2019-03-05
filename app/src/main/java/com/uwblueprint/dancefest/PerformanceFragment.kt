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
    private var tabletID: Long = -1
    private lateinit var type: String
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object {
        const val TAG_ADJUDICATION = "TAG_ADJUDICATION"
        const val TAG_PERFORMANCE = "TAG_PERFORMANCE"
        const val TYPE_COMPLETE = "TYPE_COMPLETE"
        const val TYPE_INCOMPLETE ="TYPE_INCOMPLETE"
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
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_TABLET_ID) }?.apply {
            tabletID = getLong(PerformanceActivity.TAG_TABLET_ID, -1)
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_TITLE) }?.apply {
            val title = getString(PerformanceActivity.TAG_TITLE)
            eventTitle = title
            rootView.title_performances.text = title
        }
        arguments?.takeIf { it.containsKey(PerformanceActivity.TAG_TYPE) }?.apply {
            type = getString(PerformanceActivity.TAG_TYPE, "")
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
        intent.putExtra(PerformanceActivity.TAG_TABLET_ID, tabletID)
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

    fun updateData(adjudications: HashMap<*, *>, performances: ArrayList<Performance>) {
        viewAdapter = PerformancesAdapter(adjudications, this, performances)
        list_performances.apply {
            adapter = viewAdapter
        }
    }

    fun getType(): String = type
}
