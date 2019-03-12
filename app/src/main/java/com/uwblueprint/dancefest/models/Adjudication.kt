package com.uwblueprint.dancefest.models

import java.io.Serializable

data class AdjudicationKeys(
    val ARG_ARTISTIC_MARK: String = "artisticMark",
    val ARG_AUDIO_URL: String = "audioURL",
    val ARG_CHOREO_AWARD: String = "choreoAward",
    val ARG_CUMULATIVE_MARK: String = "cumulativeMark",
    val ARG_JUDGE_NAME: String = "judgeName",
    val ARG_NOTES: String = "notes",
    val ARG_SPECIAL_AWARD: String = "specialAward",
    val ARG_TECHNICAL_MARK: String = "technicalMark"
)

data class Adjudication(
    val adjudicationId: String,
    val artisticMark: Long = -1,
    val audioURL: String? = null,
    val choreoAward: Boolean,
    val cumulativeMark: Long = -1,
    val judgeName: String,
    val notes: String,
    val specialAward: Boolean,
    val technicalMark: Long = -1
) : Serializable {
    companion object {
        val adjKeys = AdjudicationKeys()
    }
}
