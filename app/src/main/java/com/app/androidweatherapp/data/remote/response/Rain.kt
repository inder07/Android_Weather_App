package com.app.androidweatherapp.data.remote.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "`3h`")
    val jsonMember3h: Double?
): Parcelable