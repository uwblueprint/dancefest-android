package com.uwblueprint.dancefest

import java.io.Serializable

data class Adjudication(
        val artisticMark: Int = 0,
        val audioURL: String = "",
        val choreoAward: Boolean = false,
        val cumulativeMark: Int = 0,
        val judgeName: String = "",
        val notes: String = "",
        val specialAward: Boolean = false,
        val technicalMark: Int = 0
) : Serializable