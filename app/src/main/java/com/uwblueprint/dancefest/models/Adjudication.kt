package com.uwblueprint.dancefest.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Adjudication(
    @SerializedName("id") val adjudicationId: Int,
    @SerializedName("artistic_mark") val artisticMark: Int,
    @SerializedName("choreo_award") val choreoAward: Boolean,
    @SerializedName("cumulative_mark") val cumulativeMark: Double,
    @SerializedName("judge_name") val judgeName: String,
    @SerializedName("notes") val notes: String,
    @SerializedName("special_award") val specialAward: Boolean,
    @SerializedName("technical_mark") val technicalMark: Int,
    @SerializedName("tablet_id") val tabletId: Int,
    @SerializedName("audio_url") val audioURL: String? = null,
    @SerializedName("audio_length") val audioLength: Int? = null
) : Serializable

data class AdjudicationPost(
    @SerializedName("artistic_mark") val artisticMark: Int? = null,
    @SerializedName("cumulative_mark") val cumulativeMark: Double? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("technical_mark") val technicalMark: Int? = null,
    @SerializedName("tablet_id") val tabletId: Int? = null,
    @SerializedName("audio_url") val audioURL: String? = null,
    @SerializedName("audio_length") val audioLength: Int? = null
)