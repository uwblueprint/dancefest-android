package com.uwblueprint.dancefest

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.uwblueprint.dancefest.models.Event

class PerformancePagerAdapter(
        fm: FragmentManager,
        private val event: Event,
        private val tabletID: Int
) : FragmentStatePagerAdapter(fm) {

    companion object {
        const val NUM_TABS = 2
    }

    override fun getCount(): Int = NUM_TABS

    override fun getItem(position: Int): Fragment {
        val isComplete = position != 0
        return PerformanceListFragment.newInstance(
                event,
                tabletID,
                isComplete
        )
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) "INCOMPLETE" else "COMPLETE"
    }
}
