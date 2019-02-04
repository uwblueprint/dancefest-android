package com.uwblueprint.dancefest.models

import java.io.Serializable

data class Event(
        val date: String,
        val numJudges: String,
        val eventId: String,
        val name: String
) : Serializable
