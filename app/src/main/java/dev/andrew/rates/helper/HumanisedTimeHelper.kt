package dev.andrew.rates.helper

import android.icu.util.Calendar
import android.icu.util.TimeZone
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

object HumanisedTimeHelper {
    fun humanisedTimeByNow(time: Long): String {
        val atTime = Calendar.getInstance(TimeZone.GMT_ZONE).timeInMillis
        val delta = atTime - time

        var timeUnit: Long

        timeUnit = TimeUnit.MILLISECONDS.toSeconds(delta)
        if (timeUnit <= 1 * 60) {
            return "now"
        } else if (timeUnit <= 15 * 60) {
            return "${timeUnit / 60} minutes ago"
        }

        timeUnit = TimeUnit.MILLISECONDS.toMinutes(delta)
        if (timeUnit > 0) {
            if (timeUnit <= 1) {
                return "now"
            } else if (timeUnit <= 15) {
                return "$timeUnit minutes ago"
            }
        }

        timeUnit = TimeUnit.MILLISECONDS.toHours(delta)
        if (timeUnit > 0) {
            if (timeUnit <= 15) {
                return "$timeUnit hours ago"
            }
        }

        timeUnit = TimeUnit.MILLISECONDS.toDays(delta)
        if (timeUnit == 0L) {
            val currentTime = Calendar.getInstance(TimeZone.GMT_ZONE).also {
                it.timeInMillis = delta
                it.timeZone = TimeZone.getDefault()
            }
            return "${currentTime.get(Calendar.HOUR_OF_DAY)}:${currentTime.get(Calendar.MINUTE)}"
        } else if (timeUnit == 1L) {
            return "one day ago"
        }

        return time.toString()
    }

//    fun liveHumanisedTimeByNow(time: Long): LiveData<String> {
//        return MutableLiveData(humanisedTimeByNow(time))
//    }

    fun humanisedTimeFlow(time: Long): Flow<String> =
        flow {
            while (true) {
                emit(humanisedTimeByNow(time))
                delay(1000)
            }
        }

    fun humanisedInterval(interval: Long): String {
        var timeUnit: Long

        timeUnit = TimeUnit.MILLISECONDS.toSeconds(interval)
        if (timeUnit > 0 && timeUnit < 60) {
            when(timeUnit) {
                1L -> {
                    return "every second"
                }
                else -> {
                    return "every $timeUnit seconds"
                }
            }
        }

        timeUnit = TimeUnit.MILLISECONDS.toMinutes(interval)
        if (timeUnit > 0  && timeUnit < 60) {
            when(timeUnit) {
                1L -> {
                    return "every minute"
                }
                else -> {
                    return "every $timeUnit minutes"
                }
            }
        }

        timeUnit = TimeUnit.MILLISECONDS.toHours(interval)
        if (timeUnit > 0 && timeUnit < 24) {
            when(timeUnit) {
                1L -> {
                    return "every hour"
                }
                else -> {
                    return "every $timeUnit hours"
                }
            }
        }

        timeUnit = TimeUnit.MILLISECONDS.toDays(interval)
        if (timeUnit > 0) {
            when(timeUnit) {
                1L -> {
                    return "every day"
                }
                else -> {
                    return "every $timeUnit days"
                }
            }
        }

        return "instantly"
    }
}