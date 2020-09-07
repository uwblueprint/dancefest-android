package com.uwblueprint.dancefest.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tablet(
    @SerializedName("id") val tabletId: Int,
    @SerializedName("serial") val tabletSerial: String
) : Serializable
