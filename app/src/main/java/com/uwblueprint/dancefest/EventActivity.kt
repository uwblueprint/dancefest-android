package com.uwblueprint.dancefest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.uwblueprint.dancefest.firebase.FirestoreUtils
import com.uwblueprint.dancefest.models.Event
import kotlinx.android.synthetic.main.activity_event.*

// Manages and displays the Events Page.
class EventActivity : AppCompatActivity(), EventItemListener {
    private lateinit var firestoreUtils: FirestoreUtils
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    companion object {
        const val COLLECTION_NAME = "events"
        const val DATE = "eventDate"
        const val DEFAULT = "N/A"
        const val NUM_JUDGES = "numJudges"
        const val TAG = "EVENT_ACTIVITY"
        const val TAG_EVENT = "TAG_EVENT"
        const val TITLE = "eventTitle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // Set the title of the action bar.
        setTitle(R.string.dancefest)

        var events: ArrayList<Event>

        firestoreUtils = FirestoreUtils()
        firestoreUtils.getData(COLLECTION_NAME, EventListener{ value, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed", e)
                return@EventListener
            }
            if (value == null) {
                Log.e(TAG, "Null QuerySnapshot")
                return@EventListener
            }

            events = arrayListOf()

            for (doc in value) {
                val id = doc.id
                val title = doc.data[TITLE]
                val date = doc.data[DATE]
                val numJudges = doc.data[NUM_JUDGES]
                if (title == null) Log.e(TAG, "Null title in eventId: $id")
                if (date == null) Log.e(TAG, "Null date in eventId: $id")
                if (numJudges == null) Log.e(TAG, "Null numJudges in eventId: $id")
                events.add(
                    Event(
                        name = if (title == null) DEFAULT else title as String,
                        date = date?.toString() ?: DEFAULT,
                        eventId = id,
                        numJudges = ""
                        //numJudges = if (numJudges == null) DEFAULT else numJudges as String
                    )
                )
            }

            viewAdapter = EventsAdapter(this, events)
            list_events.apply { adapter = viewAdapter }
        })

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
