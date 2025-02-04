package com.app.androidweatherapp.data.remote

import com.app.androidweatherapp.data.remote.response.WeatherLocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") appID: String = Constants.APP_ID
    ): WeatherLocationResponse

    @GET("forecast")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String,
        @Query("appid") appID: String = Constants.APP_ID
    ): WeatherLocationResponse
}