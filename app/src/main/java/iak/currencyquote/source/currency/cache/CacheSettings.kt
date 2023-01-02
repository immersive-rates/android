package iak.currencyquote.source.currency.cache

import java.util.concurrent.TimeUnit

data class CacheSettings(
    val operationUpdateInterval: Long,
    val currenciesUpdateInterval: Long,
) {
    companion object {
        @JvmStatic
        val ZERO_INTERVAL = 0L
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
    }
}
