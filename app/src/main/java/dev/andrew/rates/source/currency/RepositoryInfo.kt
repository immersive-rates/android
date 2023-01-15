package dev.andrew.rates.source.currency

import androidx.annotation.StringRes
import java.util.concurrent.TimeUnit

class RepositoryInfo(
    @Deprecated("xyi")
    val id: Int = getNextId(),
    @StringRes val labelRes: Int,
    @StringRes val shortLabelRes: Int,
    val isSupportFiat: Boolean,
    val isSupportCrypto: Boolean,
    val homePageUrl: String,
    val updateInterval: Long
) {
    companion object {
        @JvmStatic
        val ONE_SECOND_INTERVAL = TimeUnit.SECONDS.toMillis(1)
        @JvmStatic
        val FIFE_SECONDS_INTERVAL = TimeUnit.SECONDS.toMillis(5)
        @JvmStatic
        val FIFE_MINUTES_INTERVAL = TimeUnit.MINUTES.toMillis(5)
        @JvmStatic
        val FIFTEEN_MINUTES_INTERVAL = TimeUnit.MINUTES.toMillis(15)
        @JvmStatic
        val ONE_DAY_INTERVAL = TimeUnit.DAYS.toMillis(1)

        private var nextId = 5
        private fun getNextId(): Int {
            return nextId++
        }
    }
}