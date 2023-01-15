package dev.andrew.rates.data

import android.icu.util.Calendar
import android.icu.util.TimeZone
import dev.andrew.rates.source.currency.RepositoryInfo

class ExchangeOperation(
    val repositoryInfo: RepositoryInfo,
    val fromName: String,
    val fromCount: Float,
    val toName: String,
    val toCount: Float,
    val at: Long = Calendar.getInstance(TimeZone.GMT_ZONE).timeInMillis
)