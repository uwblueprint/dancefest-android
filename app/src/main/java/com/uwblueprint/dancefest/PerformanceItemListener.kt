package com.uwblueprint.dancefest

import com.uwblueprint.dancefest.models.Adjudication
import com.uwblueprint.dancefest.models.Performance

interface PerformanceItemListener {
    fun onItemClicked(adjudication: Adjudication?, performance: Performance)
}
