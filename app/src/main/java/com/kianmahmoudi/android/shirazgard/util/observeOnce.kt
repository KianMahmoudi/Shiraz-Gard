package com.kianmahmoudi.android.shirazgard.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {

        override fun onChanged(value: T) {
            removeObserver(this)
            observer.onChanged(value)
        }
    })
}
