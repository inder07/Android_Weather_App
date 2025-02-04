package com.app.androidweatherapp.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import com.app.androidweatherapp.data.remote.response.Rain
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Rain")
data class RainEntity(
    val jsonMember3h: Double?
): Parcelable {
    @Ignore
    constructor(rain: Rain?) : this(
        jsonMember3h = rain?.jsonMember3h
    )
}