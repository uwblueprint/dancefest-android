package com.uwblueprint.dancefest.api

import com.uwblueprint.dancefest.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface DancefestAPI {

    @GET("events")
    fun getEvents(): Call<Map<String, Event>>
}
