package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.Performance

class PerformancePagerAdapter(
    private val adjudications: HashMap<String, Adjudication>,
    private val completePerformances: ArrayList<Performance>,
    private val event: Event,
    private val incompletePerformances: ArrayList<Performance>,
    fm: FragmentManager,
    private val tabletID: Long
) : FragmentStatePagerAdapter(fm) {

    override fun getCount() = PerformanceActivity.NUM_ITEMS

    override fun getItem(position: Int): Fragment {
        val fragment = PerformanceFragment()
        fragment.arguments = Bundle().apply {
            putSerializable(PerformanceActivity.TAG_ADJUDICATIONS, adjudications)
            putString(PerformanceActivity.TAG_EVENT_ID, event.eventId)
            putLong(PerformanceActivity.TAG_TABLET_ID, tabletID)
            putString(PerformanceActivity.TAG_TITLE, event.name)
            putParcelableArrayList(PerformanceActivity.TAG_PERFORMANCES,
                if (position == 0) incompletePerformances else completePerformances)
            putString(PerformanceActivity.TAG_TYPE,
                if (position == 0) PerformanceFragment.TYPE_INCOMPLETE
                else PerformanceFragment.TYPE_COMPLETE)
        }
        return fragment
    }

    override fun getItemPosition(`object`: Any): Int {
        if (`object` is PerformanceFragment) {
            val performances = if (`object`.getType() == PerformanceFragment.TYPE_COMPLETE)
                completePerformances
            else
                incompletePerformances
            `object`.updateData(adjudications, performances)
        }
        return super.getItemPosition(`object`)
    }
}
