package iak.currencyquote.helper

import android.content.Context
import androidx.core.os.ConfigurationCompat
import java.util.*

object LocalesHelper {
    fun getUserLocale(context: Context): Locale {
        return ConfigurationCompat.getLocales(context.resources.configuration).get(0) ?: Locale.getDefault()
    }
}