package com.uwblueprint.dancefest.api

import com.uwblueprint.dancefest.models.*
import retrofit2.Call
import retrofit2.http.*

interface DancefestAPI {

    // Events
    @GET("/api/events")
    fun getEvents(): Call<Map<String, Event>>

    // Performances
    @GET("/api/events/{eventId}/performances")
    fun getPerformances(@Path("eventId") eventId: Int): Call<Map<String, Performance>>

    // Adjudications
    @GET("/api/performances/{performanceId}/adjudications")
    fun getAdjudications(@Path("performanceId") performanceId: Int,
                         @Query("tablet_id") tabletId: Int): Call<Map<String, Adjudication>>

    @POST("/api/performances/{performanceId}/adjudications")
    fun createAdjudication(@Path("performanceId") performanceId: Int,
                           @Body adjudication: AdjudicationPost): Call<Adjudication>

    @POST("/api/adjudications/{adjudicationId}")
    fun updateAdjudication(@Path("adjudicationId") adjudicationId: Int,
                           @Body adjudication: AdjudicationPost): Call<Adjudication>

    // Tablet
    @GET("/api/tablets/{tabletSerial}")
    fun getOrCreateTablet(@Path("tabletSerial") tabletSerial: String): Call<Tablet>
}
