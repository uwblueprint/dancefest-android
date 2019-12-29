package com.uwblueprint.dancefest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uwblueprint.dancefest.api.DancefestClient
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.PAPair


class PerformanceListFragment private constructor() : Fragment() {

    private val dancefestClient = DancefestClient()
    private val service = dancefestClient.getInstance()

    lateinit var event: Event
    var tabletId: Int = -1
    var isComplete: Boolean = false

    private var paPairs: List<PAPair> = arrayListOf()

    private var performancesRecyclerView: RecyclerView? = null
    private var performancesAdapter: PerformancesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            event = args.getSerializable(TAG_EVENT) as Event
            tabletId = args.getInt(TAG_TABLET_ID)
            isComplete = args.getBoolean(TAG_IS_COMPLETE)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_performance, container, false)

        performancesAdapter = PerformancesAdapter(tabletId, event)
        performancesRecyclerView = rootView.findViewById(R.id.performance_recycler_view)
        performancesRecyclerView!!.layoutManager = LinearLayoutManager(context)
        performancesRecyclerView!!.adapter = performancesAdapter

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchPerformancesAndAdjudications()
    }

    private fun fetchPerformancesAndAdjudications() {
        dancefestClient.call(service.getAdjudications(event.eventId, tabletId)) {
            onResponse = { response ->
                val pairs = response.body()
                if (pairs != null) {
                    paPairs = if (isComplete) {
                        pairs.filter { it.adjudication?.tabletId == tabletId }
                    } else {
                        pairs.filter { it.adjudication == null }
                    }
                }
                performancesAdapter?.updatePerformances(paPairs)
            }
            onFailure = {
                Log.e("YEET", it?.message)
            }
        }
    }

    companion object {

        const val TAG_ADJUDICATIONS = "TAG_ADJUDICATIONS"
        const val TAG_EVENT = "TAG_EVENT"
        const val TAG_TABLET_ID = "TAG_TABLET_ID"
        const val TAG_PERFORMANCES = "TAG_PERFORMANCES"
        const val TAG_IS_COMPLETE = "TAG_IS_COMPLETE"

        fun newInstance(
                event: Event,
                tabletId: Int,
                isComplete: Boolean
        ): PerformanceListFragment {
            val fragment = PerformanceListFragment()
            val args = Bundle()
            args.putSerializable(TAG_EVENT, event)
            args.putInt(TAG_TABLET_ID, tabletId)
            args.putBoolean(TAG_IS_COMPLETE, isComplete)
            fragment.arguments = args
            return fragment
        }
    }


}
