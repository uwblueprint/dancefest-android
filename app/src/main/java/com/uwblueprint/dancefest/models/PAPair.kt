package com.uwblueprint.dancefest.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PAPair(
        @SerializedName("performance") val performance: Performance,
        @SerializedName("adjudication") val adjudication: Adjudication?
) : Serializable