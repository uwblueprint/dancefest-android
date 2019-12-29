package com.uwblueprint.dancefest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import com.uwblueprint.dancefest.api.DancefestClient
import com.uwblueprint.dancefest.models.Event
import kotlinx.android.synthetic.main.activity_event.*

// Manages and displays the Events Page.
class EventActivity : AppCompatActivity() {
    private var dancefestClient: DancefestClient = DancefestClient()
    private var eventsAdapter: EventsAdapter = EventsAdapter()

    companion object {
        const val TAG_EVENT = "TAG_EVENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // Set the title of the action bar.
        setTitle(R.string.dancefest)

        dancefestClient = DancefestClient()

        // Initialize RecyclerView.
        events_recycler_view.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(this@EventActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        val service = dancefestClient.getInstance()
        dancefestClient.call(service.getEvents()) {
            onResponse = {
                if (it.body() != null) {
                    val events = arrayListOf<Event>()
                    events.addAll(it.body()!!.values)
                    eventsAdapter.updateEvents(events)
                }
            }
            onFailure = {
                Log.e("GET /api/events", it?.toString())
            }
        }
    }
}
