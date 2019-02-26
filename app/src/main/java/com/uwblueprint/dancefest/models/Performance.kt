package com.uwblueprint.dancefest.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class PerformanceKeys(
    val ARG_ACADEMIC_LEVEL: String = "academicLevel",
    val ARG_CHOREOGRAPHERS: String = "choreographers",
    val ARG_COMPETITION_LEVEL: String = "competitionLevel",
    val ARG_DANCE_ENTRY: String = "danceEntry",
    val ARG_DANCE_STYLE: String = "danceStyle",
    val ARG_DANCE_TITLE: String = "danceTitle",
    val ARG_PERFORMERS: String = "performers",
    val ARG_SCHOOL: String = "school",
    val ARG_SIZE: String = "size"
)

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
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(academicLevel)
        parcel.writeString(choreographers)
        parcel.writeString(competitionLevel)
        parcel.writeString(danceEntry)
        parcel.writeString(danceStyle)
        parcel.writeString(danceTitle)
        parcel.writeString(performanceId)
        parcel.writeString(performers)
        parcel.writeString(school)
        parcel.writeString(size)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Performance> {
        override fun createFromParcel(parcel: Parcel) = Performance(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Performance>(size)

        val perfKeys = PerformanceKeys()
    }
}
