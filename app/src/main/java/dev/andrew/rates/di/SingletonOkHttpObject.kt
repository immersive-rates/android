package dev.andrew.rates.di

import android.content.Context
import dev.andrew.rates.CurrencyRepositoryClientListener
import dev.andrew.rates.source.RepositoryConnectionStatus
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy


object SingletonOkHttpObject {
    private lateinit var aApplicationContext: Context
    fun provideApplicationContext(context: Context) {
        aApplicationContext = context
    }

    private val repositoryConnectionStatus = SingletonAppObject.repositoryConnectionStatus

    val buildCurrencyRepositoryHttpClient: OkHttpClient
    get() = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .eventListener(CurrencyRepositoryClientListener(repositoryConnectionStatus))
        .build()
}