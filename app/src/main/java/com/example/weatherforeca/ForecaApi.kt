package com.example.weatherforeca

import ForecaAuthRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ForecaApi {
    @POST("/authorize/token?expire_hours=-1")
    fun authenticate(@Body request: ForecaAuthRequest): Call<ForecaAuthResponse>

    @GET("/api/v1/location/search/{query}")
    fun getLocations(@Header("Authorization") token: String, @Path("query") query: String): Call<LocationsResponse>

    @GET("api/v1/current/{location}")
    fun getCurrentWeather(@Header("Authorization") token: String, @Path("location") locationId: Int): Call<ForecastResponse>
}