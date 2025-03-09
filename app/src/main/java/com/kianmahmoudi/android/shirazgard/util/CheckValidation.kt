package com.kianmahmoudi.android.shirazgard.util

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false

    if (!password.any { it.isDigit() }) return false

    if (!password.any { it.isUpperCase() }) return false

    if (!password.any { it.isLowerCase() }) return false

    if (!password.any { !it.isLetterOrDigit() }) return false

    return true
}
