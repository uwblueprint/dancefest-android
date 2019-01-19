package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_performance.*

class PerformanceActivity : FragmentActivity() {

    private lateinit var pagerAdapter: FragmentPagerAdapter

    companion object {
        const val NUM_ITEMS = 2
        const val TITLE_COMPLETE = "COMPLETE"
        const val TITLE_INCOMPLETE = "INCOMPLETE"
        const val ARG_TEST = "TEST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        performance_toolbar.setTitle(R.string.title_performances)

        pagerAdapter = PerformancePagerAdapter(supportFragmentManager)
        view_pager.adapter = pagerAdapter
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager.currentItem = tab.position
            }

        })
    }

    class PerformancePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount() = NUM_ITEMS
        override fun getItem(position: Int): Fragment {
            val fragment = PerformanceFragment()
            val title = if (position == 0) "a" else TITLE_COMPLETE
            fragment.arguments = Bundle().apply {
                putString(ARG_TEST, title)
            }
            return fragment
        }
    }
}