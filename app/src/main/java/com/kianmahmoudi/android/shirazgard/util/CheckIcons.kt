package com.kianmahmoudi.android.shirazgard.util

import android.content.res.Resources
import com.kianmahmoudi.android.shirazgard.R

fun checkIcon(icCode: String): Int {
    var ic: Int = 0;
    when (icCode) {
        "01d" -> ic = R.drawable._01d
        "02d" -> ic = R.drawable._02d
        "03d" -> ic = R.drawable._03d
        "04d" -> ic = R.drawable._04d
        "09d" -> ic = R.drawable._09d
        "10d" -> ic = R.drawable._10d
        "11d" -> ic = R.drawable._11d
        "13d" -> ic = R.drawable._13d
        "50d" -> ic = R.drawable._50d
        "01n" -> ic = R.drawable._01n
        "02n" -> ic = R.drawable._02n
        "03n" -> ic = R.drawable._03n
        "04n" -> ic = R.drawable._04n
        "09n" -> ic = R.drawable._09n
        "10n" -> ic = R.drawable._10n
        "11n" -> ic = R.drawable._11n
        "13n" -> ic = R.drawable._13n
        "50n" -> ic = R.drawable._50n
        else -> null
    }
    return ic
}