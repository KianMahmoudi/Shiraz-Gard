package com.kianmahmoudi.android.shirazgard.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val objectId: String,
    val faName: String,
    val enName: String,
    val type: String,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    var isFavorite: Boolean = false
)