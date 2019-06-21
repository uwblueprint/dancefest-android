package com.uwblueprint.dancefest.models

import java.io.Serializable
import java.util.Date

data class Event(
    val eventDate: Date,
    val numJudges: Int,
    val eventId: Int,
    val eventTitle: String
) : Serializable
