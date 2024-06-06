package com.example.food_ordering

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

fun setLocaleBasedOnRegion(context: Context) {
    val locale: Locale = if (Locale.getDefault().country == "VN") {
        Locale("vi")
    } else {
        Locale("en")
    }
    Locale.setDefault(locale)
    val config = Configuration()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocale(locale)
    } else {
        config.locale = locale
    }
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
