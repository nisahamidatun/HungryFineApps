package com.learning.orderfoodappsch3.utils

import android.content.Context
import androidx.annotation.StringRes

class AssetWrapper(private val appContext: Context) {
    fun getString(@StringRes id: Int): String {
        return appContext.getString(id)
    }
}
