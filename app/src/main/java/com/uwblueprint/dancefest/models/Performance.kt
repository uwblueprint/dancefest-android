package com.uwblueprint.dancefest.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

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

        const val ARG_ACADEMIC_LEVEL = "academicLevel"
        const val ARG_CHOREOGRAPHERS = "choreographers"
        const val ARG_COMPETITION_LEVEL = "competitionLevel"
        const val ARG_DANCE_ENTRY = "danceEntry"
        const val ARG_DANCE_STYLE = "danceStyle"
        const val ARG_DANCE_TITLE = "danceTitle"
        const val ARG_PERFORMERS = "performers"
        const val ARG_SCHOOL = "school"
        const val ARG_SIZE = "size"
    }
}
