package com.uwblueprint.dancefest.models

import java.io.Serializable

data class Adjudication(
        val artisticMark: Int = -1,
        val audioURL: String,
        val choreoAward: Boolean,
        val cumulativeMark: Int = -1,
        val judgeName: String,
        val notes: String,
        val specialAward: Boolean,
        val technicalMark: Int = -1
) : Serializable
