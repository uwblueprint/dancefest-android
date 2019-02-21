package com.uwblueprint.dancefest

import com.uwblueprint.dancefest.models.Event

interface EventItemListener {
    fun onItemClicked(event: Event)
}
