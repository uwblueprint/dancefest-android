package com.uwblueprint.dancefest.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class PerformanceKeys(
    val ARG_ACADEMIC_LEVEL: String = "academicLevel",
    val ARG_CHOREOGRAPHERS: String = "choreographers",
    val ARG_COMPETITION_LEVEL: String = "competitionLevel",
    val ARG_DANCE_ENTRY: String = "danceEntry",
    val ARG_DANCE_SIZE: String = "danceSize",
    val ARG_DANCE_STYLE: String = "danceStyle",
    val ARG_DANCE_TITLE: String = "danceTitle",
    val ARG_PERFORMERS: String = "performers",
    val ARG_SCHOOL: String = "school"
)

data class Performance(
    val academicLevel: String,
    val choreographers: ArrayList<String>,
    val competitionLevel: String,
    val danceEntry: Int,
    val danceSize: String,
    val danceStyle: String,
    val danceTitle: String,
    val eventId: Int,
    val performanceId: Int,
    val performers: ArrayList<String>,
    val school: String
): Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()?.split(PerformanceConstants.LIST_SEPARATOR),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()?.split(PerformanceConstants.LIST_SEPARATOR),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(academicLevel)
        parcel.writeString(choreographers.joinToString(PerformanceConstants.LIST_SEPARATOR))
        parcel.writeString(competitionLevel)
        parcel.writeInt(danceEntry)
        parcel.writeString(danceSize)
        parcel.writeString(danceStyle)
        parcel.writeString(danceTitle)
        parcel.writeInt(performanceId)
        parcel.writeString(performers.joinToString(PerformanceConstants.LIST_SEPARATOR))
        parcel.writeString(school)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Performance> {
        override fun createFromParcel(parcel: Parcel) = Performance(parcel)
        override fun newArray(size: Int) = arrayOfNulls<Performance>(size)

        val perfKeys = PerformanceKeys()
    }

    object PerformanceConstants {
        const val LIST_SEPARATOR = ","
    }
}
