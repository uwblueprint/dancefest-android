package com.uwblueprint.dancefest

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.uwblueprint.dancefest.api.DancefestClient
import com.uwblueprint.dancefest.api.DancefestService
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.Performance

class PerformanceActivity : AppCompatActivity() {

    private var adjudications: HashMap<Int, Adjudication> = hashMapOf()

    private var completePerformances: ArrayList<Performance> = arrayListOf()
    private var incompletePerformances: ArrayList<Performance> = arrayListOf()

    private var dancefestClient: DancefestClient = DancefestClient()
    private var service: DancefestService = dancefestClient.getInstance()

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private lateinit var event: Event
    private lateinit var performancesPagerAdapter: PerformancePagerAdapter

    private var tabletId: Int = -1

    companion object {
        const val TAG_ADJUDICATIONS = "TAG_ADJUDICATIONS"
        const val TAG_EVENT = "TAG_EVENT"
        const val TAG_EVENT_ID = "TAG_EVENT_ID"
        const val TAG_TITLE = "TAG_TITLE"
        const val TAG_TABLET_ID = "TAG_TABLET_ID"
        const val TAG_PERFORMANCES = "TAG_PERFORMANCES"
        const val TAG_IS_COMPLETE = "TAG_IS_COMPLETE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        if (intent != null) {
            event = intent.getSerializableExtra(EventActivity.TAG_EVENT) as Event
        }

        setupPerformanceToolbar()

        dancefestClient = DancefestClient()
        service = dancefestClient.getInstance()

        initPagerAdapter()
    }

    private fun setupPerformanceToolbar() {
        toolbar = findViewById(R.id.performance_toolbar)
        toolbar.title = "'" + event.eventTitle + "' Performances"
        setSupportActionBar(toolbar)
    }

    private fun initPagerAdapter() {
        dancefestClient.call(service.getOrCreateTablet(Build.SERIAL)) {
            onResponse = { response ->

                val tablet = response.body()
                if (tablet != null) {
                    tabletId = tablet.tabletId
                }

                performancesPagerAdapter = PerformancePagerAdapter(
                        supportFragmentManager,
                        event,
                        tabletId
                )

                viewPager = findViewById(R.id.view_pager)
                viewPager.adapter = performancesPagerAdapter

                tabLayout = findViewById(R.id.tab_layout)
                tabLayout.setupWithViewPager(viewPager)
            }
            onFailure = { Log.e("Get Tablet by Serial", it?.toString()) }
        }
    }
}
