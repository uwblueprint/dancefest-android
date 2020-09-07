package com.uwblueprint.dancefest.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class Event(
    @SerializedName("id") val eventId: Int,
    @SerializedName("event_date") val eventDate: Date?,
    @SerializedName("num_judges") val numJudges: Int,
    @SerializedName("event_title") val eventTitle: String
) : Serializable
