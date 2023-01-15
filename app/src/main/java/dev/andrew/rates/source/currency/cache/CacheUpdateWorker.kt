package dev.andrew.rates.source.currency.cache

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CacheUpdateWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}