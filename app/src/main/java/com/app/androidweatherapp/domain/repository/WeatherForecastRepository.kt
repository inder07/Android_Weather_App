package com.app.androidweatherapp.domain.repository

import android.content.Context
import com.app.androidweatherapp.domain.model.WeatherLocationResponseDomain
import com.app.androidweatherapp.utils.ApiResult
import kotlinx.coroutines.flow.Flow

interface WeatherForecastRepository {
    suspend fun getWeatherForecast(
        context: Context,
        latitude: Double,
        longitude: Double,
        units: String
    ): Flow<ApiResult<WeatherLocationResponseDomain>>

    suspend fun getWeatherByCity(
        cityName: String,
        units: String
    ): Flow<ApiResult<WeatherLocationResponseDomain>>
}