package com.uwblueprint.dancefest.api

import com.uwblueprint.dancefest.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DancefestService {

    // Events
    @GET("/api/events")
    fun getEvents(): Call<Map<String, Event>>

    // Performances
    @GET("/api/events/{eventId}/performances")
    fun getPerformances(@Path("eventId") eventId: Int): Call<Map<String, Performance>>

    // Adjudications
    @GET("/api/performances/{performanceId}/{tabletId}/adjudications")
    fun getAdjudications(@Path("performanceId") performanceId: Int,
                         @Path("tabletId") tabletId: Int): Call<List<PAPair>>

    @POST("/api/performances/{performanceId}/adjudications")
    fun createAdjudication(@Path("performanceId") performanceId: Int,
                           @Body adjudication: AdjudicationPost): Call<Adjudication>

    @POST("/api/adjudications/{adjudicationId}")
    fun updateAdjudication(@Path("adjudicationId") adjudicationId: Int,
                           @Body adjudication: AdjudicationPost): Call<Adjudication>

    // Tablet
    @GET("/api/tablets/{tabletSerial}")
    fun getOrCreateTablet(@Path("tabletSerial") tabletSerial: String): Call<Tablet>

    //Awards
    @GET("/api/awards/{eventId}")
    fun getAwards(@Path("eventId") eventId: Int): Call<List<String>>
}
