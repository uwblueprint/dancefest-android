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
) : Serializable {
    companion object {
        const val ARG_ARTISTIC_MARK = "artisticMark"
        const val ARG_AUDIO_URL = "audioURL"
        const val ARG_CHOREO_AWARD = "choreoAward"
        const val ARG_CUMULATIVE_MARK = "cumulativeMark"
        const val ARG_JUDGE_NAME = "judgeName"
        const val ARG_NOTES = "notes"
        const val ARG_SPECIAL_AWARD = "specialAward"
        const val ARG_TECHNICAL_MARK = "technicalMark"
    }
}
