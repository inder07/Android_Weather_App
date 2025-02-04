package com.app.androidweatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.androidweatherapp.data.local.dao.CurrentWeatherDao
import com.app.androidweatherapp.data.local.dao.ForecastDao
import com.app.androidweatherapp.data.local.entity.WeatherListEntity
import com.app.androidweatherapp.data.local.entity.WeatherLocationEntity
import com.app.androidweatherapp.utils.DataConverter

@Database(
    entities = [
        WeatherLocationEntity::class,
        WeatherListEntity::class
    ],
    version = 1
)

@TypeConverters(DataConverter::class)
abstract class WeatherAppDatabase : RoomDatabase() {
    abstract val currentWeatherDao: CurrentWeatherDao
    abstract val forecastDao: ForecastDao
}