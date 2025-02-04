package com.app.androidweatherapp.data.repositoryImpl

import android.content.Context
import com.app.androidweatherapp.data.local.WeatherAppDatabase
import com.app.androidweatherapp.data.mappers.WeatherForecastEntityMapper
import com.app.androidweatherapp.data.mappers.WeatherForecastMapperData
import com.app.androidweatherapp.data.mappers.WeatherForecastMapperDomain
import com.app.androidweatherapp.data.remote.WeatherApiService
import com.app.androidweatherapp.domain.model.WeatherLocationResponseDomain
import com.app.androidweatherapp.domain.repository.WeatherForecastRepository
import com.app.androidweatherapp.utils.ApiResult
import com.app.androidweatherapp.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherForecastRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val weatherAppDatabase: WeatherAppDatabase
): WeatherForecastRepository {

    private val weatherForecastEntityMapper: WeatherForecastEntityMapper = WeatherForecastEntityMapper()
    private val weatherForecastMapperDomain : WeatherForecastMapperDomain = WeatherForecastMapperDomain()
    private val weatherForecastMapperData: WeatherForecastMapperData = WeatherForecastMapperData()

    override suspend fun getWeatherForecast(
        context: Context,
        latitude: Double,
        longitude: Double,
        units: String
    ): Flow<ApiResult<WeatherLocationResponseDomain>> {
        return flow {
            emit(ApiResult.Loading(true))

            val localWeatherForecast = weatherAppDatabase.forecastDao.getWeatherForecast()

            val shouldLoadLocalWeather = localWeatherForecast?.list?.isNotEmpty() == true && !NetworkUtils.isNetworkAvailable(context)

            if (shouldLoadLocalWeather) {
                emit(ApiResult.Success(
                    data = localWeatherForecast.let { weatherForecastEntity ->
                        weatherForecastEntityMapper.mapToWeatherLocationEntity(weatherForecastEntity)
                    }
                ))

                emit(ApiResult.Loading(false))
                return@flow
            }

            val getCurrentWeatherForecast = try {
                weatherApiService.getWeatherForecast(latitude, longitude, units)
            } catch (e: IOException) {
                emit(ApiResult.Error("Error loading weather"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(ApiResult.Error(message = "Error loading weather"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(message = "Error loading weather"))
                return@flow
            }

            val weatherForecastEntities = getCurrentWeatherForecast.let { weatherForecastResponse ->
                weatherForecastMapperData.mapDataToEntity(weatherForecastResponse)
            }

            weatherForecastEntities?.let { weatherAppDatabase.forecastDao.upsertWeatherForecast(it) }

            emit(ApiResult.Success(
                weatherForecastEntities.let {
                    weatherForecastEntityMapper.mapToWeatherLocationEntity(it)
                }
            ))
            emit(ApiResult.Loading(false))
        }
    }

    override suspend fun getWeatherByCity(
        cityName: String,
        units: String
    ): Flow<ApiResult<WeatherLocationResponseDomain>> {
        return flow {
            emit(ApiResult.Loading(true))

            val getWeatherByCity = try {
                weatherApiService.getWeatherByCity(cityName, units)
            } catch (e: IOException) {
                emit(ApiResult.Error("Error loading weather"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(ApiResult.Error(message = "Error loading weather"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(message = "Error loading weather"))
                return@flow
            }

            emit(ApiResult.Success(
                weatherForecastMapperDomain.mapDataToDomain(getWeatherByCity)
            ))
            emit(ApiResult.Loading(false))
        }
    }
}