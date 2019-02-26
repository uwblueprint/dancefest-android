package com.uwblueprint.dancefest

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Event
import com.uwblueprint.dancefest.models.Performance

class PerformancePagerAdapter(
    private val adjudications: HashMap<String, Adjudication>,
    private val completePerformances: ArrayList<Performance>,
    private val event: Event,
    private val incompletePerformances: ArrayList<Performance>,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getCount() = PerformanceActivity.NUM_ITEMS

    override fun getItem(position: Int): Fragment {
        val fragment = PerformanceFragment()
        fragment.arguments = Bundle().apply {
            putSerializable(PerformanceActivity.TAG_ADJUDICATIONS, adjudications)
            putString(PerformanceActivity.TAG_TITLE, event.name)
            putParcelableArrayList(PerformanceActivity.TAG_PERFORMANCES,
                if (position == 0) incompletePerformances else completePerformances)
        }
        return fragment
    }
}