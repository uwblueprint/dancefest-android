package com.uwblueprint.dancefest.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

fun getStringListFromParcel(parcel: Parcel): ArrayList<String> {
    val stringList: ArrayList<String> = ArrayList()
    parcel.readStringList(stringList)
    return stringList
}

data class Performance(
        @SerializedName("id") val performanceId: Int,
        @SerializedName("academic_level") val academicLevel: String?,
        @SerializedName("choreographers") val choreographers: List<String>?,
        @SerializedName("competition_level") val competitionLevel: String?,
        @SerializedName("dance_entry") val danceEntry: Int,
        @SerializedName("dance_size") val danceSize: String?,
        @SerializedName("dance_style") val danceStyle: String?,
        @SerializedName("dance_title") val danceTitle: String?,
        @SerializedName("event_id") val eventId: Int,
        @SerializedName("performers") val performers: List<String>?,
        @SerializedName("school") val school: String?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            getStringListFromParcel(parcel),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            getStringListFromParcel(parcel),
            parcel.readString()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(performanceId)
        parcel.writeString(academicLevel)
        parcel.writeStringList(choreographers)
        parcel.writeString(competitionLevel)
        parcel.writeInt(danceEntry)
        parcel.writeString(danceSize)
        parcel.writeString(danceStyle)
        parcel.writeString(danceTitle)
        parcel.writeInt(performanceId)
        parcel.writeStringList(performers)
        parcel.writeString(school)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Performance> {
        override fun createFromParcel(parcel: Parcel) = Performance(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Performance>(size)
    }
}
