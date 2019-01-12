package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_event.*

class EventActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // set the title of the action bar
        setTitle(R.string.dancefest)

        // create temporary data
        val events = arrayListOf(
                Event("OSSDF2018 - Dance to the Rhythm", "May 22, 2018", "a"),
                Event("OSSDF2018 - Theme", "Sept 18, 2018", "b"),
                Event("OSSDF2017 - Theme", "May 21, 2017", "c"),
                Event("OSSDF2017 - Theme", "Sept 20, 2017", "d"),
                Event("OSSDF2016 - Theme", "May 14, 2016", "e"))

        // initialize recyclerview
        viewManager = LinearLayoutManager(this)
        viewAdapter = EventsAdapter(events)
        list_events.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}