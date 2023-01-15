package dev.andrew.rates.di

import android.content.Context
import dev.andrew.rates.ConnectStatusManager
import dev.andrew.rates.helper.LocalesHelper
import dev.andrew.rates.source.CurrencySourceManager
import dev.andrew.rates.source.RepositoryConnectionStatus
import dev.andrew.rates.source.AppSettings
import dev.andrew.rates.source.ExchangeHistorySettings

object SingletonAppObject {
    private lateinit var aApplicationContext: Context
    fun provideApplicationContext(context: Context) {
        aApplicationContext = context
    }

    val sharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        aApplicationContext.getSharedPreferences("APP_SHARES", Context.MODE_PRIVATE)
    }
    val currencySourceManager by lazy(LazyThreadSafetyMode.NONE) {
        CurrencySourceManager(appSetting)
    }
    val appSetting by lazy(LazyThreadSafetyMode.NONE) {
        AppSettings(sharedPreferences, LocalesHelper.getUserLocale(aApplicationContext))
    }
    val repositoryConnectionStatus by lazy(LazyThreadSafetyMode.NONE) {
        RepositoryConnectionStatus()
    }
    val connectStatusManager by lazy(LazyThreadSafetyMode.NONE) {
        ConnectStatusManager(aApplicationContext)
    }
    val exchangeHistory by lazy(LazyThreadSafetyMode.NONE) {
        ExchangeHistorySettings(sharedPreferences, currencySourceManager)
    }
}