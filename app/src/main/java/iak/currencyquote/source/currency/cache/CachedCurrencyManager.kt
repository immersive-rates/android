package iak.currencyquote.source.currency.cache

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import iak.currencyquote.source.currency.ICurrencySource
import java.util.concurrent.TimeUnit

class CachedCurrencyManager<T>(
    private val context: Context
) where T: ICurrencySource, T: ICachedCurrencySource {
    companion object {
        const val CACHE_WORK_TAG = "CachedCurrencyManager_CACHE_WORK_TAG"
        const val TAG = "CachedCurrencyManager"
    }

    private lateinit var managedSource: ICurrencySource

    init {
        addRefreshSourceTask()
    }

    fun manageSource(source: T) {
        managedSource = source
    }

    fun forceRefresh() {

    }

    private fun addRefreshSourceTask() {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(CACHE_WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<CacheUpdateWorker>(20, TimeUnit.MINUTES)
                    .build())
    }
}