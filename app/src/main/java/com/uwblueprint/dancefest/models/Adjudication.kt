package com.uwblueprint.dancefest.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AdjudicationKeys(
    val ARG_ARTISTIC_MARK: String = "artisticMark",
    val ARG_AUDIO_URL: String = "audioURL",
    val ARG_AUDIO_LENGTH: String = "audioLength",
    val ARG_CHOREO_AWARD: String = "choreoAward",
    val ARG_CUMULATIVE_MARK: String = "cumulativeMark",
    val ARG_JUDGE_NAME: String = "judgeName",
    val ARG_NOTES: String = "notes",
    val ARG_SPECIAL_AWARD: String = "specialAward",
    val ARG_TECHNICAL_MARK: String = "technicalMark"
)

data class Adjudication(
    @SerializedName("id") val adjudicationId: Int,
    @SerializedName("artistic_mark") val artisticMark: Long,
    @SerializedName("choreo_award") val choreoAward: Boolean,
    @SerializedName("cumulative_mark") val cumulativeMark: Long,
    @SerializedName("judge_name") val judgeName: String,
    @SerializedName("notes") val notes: String,
    @SerializedName("special_award") val specialAward: Boolean,
    @SerializedName("technical_mark") val technicalMark: Long,
    @SerializedName("audio_url") val audioURL: String? = null,
    @SerializedName("audio_length") val audioLength: Int? = null
) : Serializable {
    companion object {
        val adjKeys = AdjudicationKeys()
    }
}
