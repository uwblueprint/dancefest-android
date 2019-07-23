package com.uwblueprint.dancefest.api

import com.uwblueprint.dancefest.models.*
import retrofit2.Call
import retrofit2.http.*

interface DancefestAPI {

    // Events
    @GET("events")
    fun getEvents(): Call<Map<String, Event>>

    // Performances
    @GET("events/{eventId}/performances")
    fun getPerformances(@Path("eventId") eventId: Int): Call<Map<String, Performance>>

    // Adjudications
    @GET("performances/{performanceId}/adjudications")
    fun getAdjudicationsByPerformanceId(@Path("performanceId") performanceId: Int,
                                        @Query("tablet_id") tabletId: Int):
        Call<Map<String, Adjudication>>

    // Tablet
    @GET("tablets/{tabletSerial}")
    fun getOrCreateTablet(@Path("tabletSerial") tabletSerial: String): Call<Tablet>
}
