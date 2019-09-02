package com.uwblueprint.dancefest

import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.uwblueprint.dancefest.api.DancefestAPI
import com.uwblueprint.dancefest.api.DancefestClientAPI
import com.uwblueprint.dancefest.models.*
import kotlinx.android.synthetic.main.activity_performance.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class PerformanceActivity : AppCompatActivity() {

    private var adjudications: HashMap<Int, Adjudication> = hashMapOf()

    private var completePerformances: ArrayList<Performance> = arrayListOf()
    private var incompletePerformances: ArrayList<Performance> = arrayListOf()

    private lateinit var dancefestClientAPI: DancefestClientAPI
    private lateinit var api: DancefestAPI
    private lateinit var event: Event
    private lateinit var pagerAdapter: FragmentStatePagerAdapter

    private var tabletID: Int = -1

    companion object {
        const val NUM_ITEMS = 2
        const val TAG_ADJUDICATIONS = "TAG_ADJUDICATIONS"
        const val TAG_EVENT_ID = "TAG_EVENT_ID"
        const val TAG_PERFORMANCES = "TAG_PERFORMANCES"
        const val TAG_TABLET_ID = "tabletID"
        const val TAG_TITLE = "TAG_TITLE"
        const val TAG_IS_COMPLETE = "TAG_IS_COMPLETE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        performance_toolbar.setTitle(R.string.title_performances)
        setSupportActionBar(performance_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dancefestClientAPI = DancefestClientAPI()
        api = dancefestClientAPI.getInstance()

        if (intent != null) {
            event = intent.getSerializableExtra(EventActivity.TAG_EVENT) as Event
        }

        initPerformancesAdapter()
    }

    override fun onResume() {
        super.onResume()
        fetchPerformancesData()
    }

    override fun onSupportNavigateUp(): Boolean {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        onBackPressed()
        return true
    }

    private fun initPerformancesAdapter() {
        // Initialize tablet id here
        dancefestClientAPI.call(api.getOrCreateTablet(Build.SERIAL)) {
            onResponse = {
                Log.e("Get Tablet by Serial", it.body().toString())
                if (it.body() != null) {
                    tabletID = it.body()!!.tabletId

                    runOnUiThread {
                        pagerAdapter = PerformancePagerAdapter(
                            adjudications,
                            completePerformances,
                            event,
                            incompletePerformances,
                            supportFragmentManager,
                            tabletID
                        )
                        view_pager.adapter = pagerAdapter
                        view_pager.addOnPageChangeListener(
                            TabLayout.TabLayoutOnPageChangeListener(tab_layout))
                        tab_layout.addOnTabSelectedListener(
                            object : TabLayout.OnTabSelectedListener {
                                override fun onTabReselected(tab: TabLayout.Tab) {}
                                override fun onTabUnselected(tab: TabLayout.Tab) {}
                                override fun onTabSelected(tab: TabLayout.Tab) {
                                    view_pager.currentItem = tab.position
                                }
                            })
                    }
                }
            }
            onFailure = { Log.e("Get Tablet by Serial", it?.toString()) }
        }
    }

    private fun fetchPerformancesData() {
        // Fetching performances from DB to update UI
        dancefestClientAPI.call(api.getPerformances(event.eventId)) {
            onResponse = {
                var performancesList = ArrayList<Performance>()
                if (it.body() != null) {
                    performancesList = ArrayList(it.body()!!.values)
                }
                updatePerformances(performancesList)
            }
            onFailure = { Log.e("Get Performances by Event Id", it?.toString()) }
        }
    }

    private fun updatePerformances(performances: ArrayList<Performance>) {
        completePerformances.clear()
        incompletePerformances.clear()
        adjudications.clear()

        val countDownLatch = CountDownLatch(performances.size)

        for (performance in performances) {
            val performanceId = performance.performanceId

            dancefestClientAPI.call(api.getAdjudications(performanceId, tabletID)) {
                onResponse = {
                    var adjudicationsList = ArrayList<Adjudication>()
                    if (it.body() != null) {
                        adjudicationsList = ArrayList(it.body()!!.values)
                    }

                    if (!adjudicationsList.isEmpty()) {
                        adjudications[performanceId] = adjudicationsList[0]
                        completePerformances.add(performance)
                    } else {
                        incompletePerformances.add(performance)
                    }
                    countDownLatch.countDown()
                }
                onFailure = {
                    Log.e("Get Adjudications by Performance Id", it?.toString())
                    // Count down in failure too so we don't keep waiting for it
                    countDownLatch.countDown()
                }
            }
        }

        thread {
            countDownLatch.await()
            runOnUiThread {
                // Sort by title then by danceEntry within titles
                completePerformances.sortBy { perf -> perf.danceTitle }
                incompletePerformances.sortBy {perf -> perf.danceTitle }
                completePerformances.sortBy { perf -> perf.danceEntry }
                incompletePerformances.sortBy { perf -> perf.danceEntry }
                pagerAdapter.notifyDataSetChanged()
            }
        }
    }
}
