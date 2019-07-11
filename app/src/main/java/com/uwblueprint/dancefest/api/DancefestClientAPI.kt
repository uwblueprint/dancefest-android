package com.uwblueprint.dancefest.api

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DancefestClientAPI{
    companion object {
        //TODO: Put URL in env file
        private const val BASE_URL = "http://10.0.2.2:5000/"
        private const val DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z"

        private var dancefestAPI:DancefestAPI

        init {
            val gson = GsonBuilder()
                .setLenient()
                .setDateFormat(DATE_FORMAT)
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            dancefestAPI = retrofit.create(DancefestAPI::class.java)
        }
    }

    fun getInstance(): DancefestAPI {
        return dancefestAPI;
    }

    /* Method to asynchronously call retrofit API, using our customized callback class
     * Usage:
     * clientApi.call(clientApi.getInstance().getEvents()) {
     *     onResponse = {}
     *     onFailure = {}
     * }
     */
    fun <T> call(call: Call<T>, callback: CallBackRt<T>.() -> Unit) {
        val callBackKt = CallBackRt<T>()
        callback.invoke(callBackKt)
        call.enqueue(callBackKt)
    }

    /* Custom Callback Class
     * Allows us to nicely format and customize callbacks to Retrofit calls
     * while minimizing boilerplate
     */
    class CallBackRt<T>: Callback<T> {
        var onResponse: ((Response<T>) -> Unit)? = null
        var onFailure: ((t: Throwable?) -> Unit)? = null

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure?.invoke(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse?.invoke(response)
        }
    }
}
