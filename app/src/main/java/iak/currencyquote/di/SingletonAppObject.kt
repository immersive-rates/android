package iak.currencyquote.di

import android.content.Context
import androidx.core.os.ConfigurationCompat
import iak.currencyquote.ConnectStatusManager
import iak.currencyquote.helper.LocalesHelper
import iak.currencyquote.source.CurrencySourceManager
import iak.currencyquote.source.RepositoryConnectionStatus
import iak.currencyquote.source.AppSettings
import iak.currencyquote.source.ExchangeHistorySettings

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