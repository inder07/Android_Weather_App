package com.app.androidweatherapp.domain.model

import android.os.Parcelable
import com.app.androidweatherapp.domain.model.CityDomain
import com.app.androidweatherapp.domain.model.WeatherListDomain
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class WeatherLocationResponseDomain(
    val city: CityDomain?,
    val cnt: Int?,
    val cod: String?,
    val list: List<WeatherListDomain>? = listOf(),
    val message: Int?
): Parcelable