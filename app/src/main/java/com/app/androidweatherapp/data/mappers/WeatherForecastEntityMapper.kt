package com.app.androidweatherapp.data.mappers

import com.app.androidweatherapp.data.local.entity.CityEntity
import com.app.androidweatherapp.data.local.entity.CoordEntity
import com.app.androidweatherapp.data.local.entity.WeatherLocationEntity
import com.app.androidweatherapp.data.remote.response.Clouds
import com.app.androidweatherapp.data.remote.response.Main
import com.app.androidweatherapp.data.remote.response.Rain
import com.app.androidweatherapp.data.remote.response.Sys
import com.app.androidweatherapp.data.remote.response.Weather
import com.app.androidweatherapp.data.remote.response.WeatherList
import com.app.androidweatherapp.data.remote.response.Wind
import com.app.androidweatherapp.domain.model.CityDomain
import com.app.androidweatherapp.domain.model.CloudsDomain
import com.app.androidweatherapp.domain.model.CoordDomain
import com.app.androidweatherapp.domain.model.MainDomain
import com.app.androidweatherapp.domain.model.RainDomain
import com.app.androidweatherapp.domain.model.SysDomain
import com.app.androidweatherapp.domain.model.WeatherDomain
import com.app.androidweatherapp.domain.model.WeatherListDomain
import com.app.androidweatherapp.domain.model.WeatherLocationResponseDomain
import com.app.androidweatherapp.domain.model.WindDomain

class WeatherForecastEntityMapper {

    fun mapToWeatherLocationEntity(weatherLocationEntity: WeatherLocationEntity?) : WeatherLocationResponseDomain? {
        if (weatherLocationEntity == null) return null
        return with(weatherLocationEntity) {
            WeatherLocationResponseDomain(
                city = city?.toCityDomain(),
                cnt = cnt,
                cod = cod,
                list = list?.map { it.toWeatherListDomain() },
                message = message
            )
        }
    }

    fun CityEntity.toCityDomain() : CityDomain {
        return CityDomain(
            coord = cityCoord?.toCoordDomain(),
            country = cityCountry,
            id = cityId,
            name = cityName,
            population = cityPopulation,
            sunrise = citySunrise,
            sunset = citySunset,
            timezone = cityTimezone
        )
    }

    fun WeatherList.toWeatherListDomain() : WeatherListDomain {
        return WeatherListDomain(
            clouds = clouds?.toCloudsDomain(),
            dt = dt,
            dtTxt = dtTxt,
            main = main?.toMainDomain(),
            pop = pop,
            rain = rain?.toRainDomain(),
            sys = sys?.toSysDomain(),
            visibility = visibility,
            weather = weather?.map { it.toWeatherDomain() },
            wind = wind?.toWindDomain()
        )
    }

    fun CoordEntity.toCoordDomain() : CoordDomain {
        return CoordDomain(
            lat = lat,
            lon = lon
        )
    }

    fun Clouds.toCloudsDomain() : CloudsDomain {
        return CloudsDomain(
            all = all
        )
    }

    fun Main.toMainDomain() : MainDomain {
        return MainDomain(
            feelsLike = feelsLike,
            grndLevel = grndLevel,
            humidity = humidity,
            pressure = pressure,
            seaLevel = seaLevel,
            temp = temp,
            tempKf = tempKf,
            tempMax = tempMax,
            tempMin = tempMin
        )
    }

    fun Rain.toRainDomain() : RainDomain {
        return RainDomain(
            jsonMember3h = jsonMember3h
        )
    }

    fun Sys.toSysDomain() : SysDomain {
        return SysDomain(
            pod = pod
        )
    }

    fun Weather.toWeatherDomain() : WeatherDomain {
        return WeatherDomain(
            description = description,
            icon = icon,
            id = id,
            main = main
        )
    }

    fun Wind.toWindDomain() : WindDomain {
        return WindDomain(
            deg = deg,
            gust = gust,
            speed = speed
        )
    }
}