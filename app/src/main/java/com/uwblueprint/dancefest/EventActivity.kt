package com.uwblueprint.dancefest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.uwblueprint.dancefest.api.DancefestClientAPI
import com.uwblueprint.dancefest.models.Event
import kotlinx.android.synthetic.main.activity_event.*
import java.util.*

// Manages and displays the Events Page.
class EventActivity : AppCompatActivity(), EventItemListener {
    private lateinit var dancefestClientAPI: DancefestClientAPI
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object {
        const val TAG_EVENT = "TAG_EVENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        // Set the title of the action bar.
        setTitle(R.string.dancefest)

        dancefestClientAPI = DancefestClientAPI()

        val api = dancefestClientAPI.getInstance()
        val eventsListener = this
        dancefestClientAPI.call(api.getEvents()) {
            onResponse = {
                var events = ArrayList<Event>()
                if (it.body() != null) {
                    events = ArrayList(it.body()!!.values)
                }

                viewAdapter = EventsAdapter(eventsListener, events)
                list_events.apply { adapter = viewAdapter }
            }
            onFailure = { Log.e("Get Events", it?.toString()) }
        }

        // Initialize RecyclerView.
        viewManager = LinearLayoutManager(this)
        list_events.apply { layoutManager = viewManager }
    }

    override fun onItemClicked(event: Event) {
        val intent = Intent(this, PerformanceActivity::class.java)
        intent.putExtra(TAG_EVENT, event)
        startActivity(intent)
    }
}
