package com.kianmahmoudi.android.shirazgard.util

fun isValidPassword(password: String): Boolean {
    // شرط 1: حداقل 8 کاراکتر
    if (password.length < 8) return false

    // شرط 2: داشتن حداقل یک عدد
    if (!password.any { it.isDigit() }) return false

    // شرط 3: داشتن حداقل یک حرف بزرگ
    if (!password.any { it.isUpperCase() }) return false

    // شرط 4: داشتن حداقل یک حرف کوچک
    if (!password.any { it.isLowerCase() }) return false

    // شرط 5: داشتن حداقل یک کاراکتر خاص
    if (!password.any { !it.isLetterOrDigit() }) return false

    return true
}
