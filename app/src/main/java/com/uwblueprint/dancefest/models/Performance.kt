package com.uwblueprint.dancefest.models

import java.io.Serializable

data class Performance(
    val academicLevel: String,
    val choreographers: String,
    val competitionLevel: String,
    val danceEntry: String,
    val danceStyle: String,
    val danceTitle: String,
    val performanceId: String,
    val performers: String,
    val school: String,
    val size: String
): Serializable
