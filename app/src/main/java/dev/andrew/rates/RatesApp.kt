package dev.andrew.rates

import androidx.multidex.MultiDexApplication
import dev.andrew.rates.di.SingletonAppObject
import dev.andrew.rates.di.SingletonOkHttpObject

class RatesApp: MultiDexApplication() {
    override fun onCreate() {
        SingletonAppObject.provideApplicationContext(applicationContext)
        SingletonOkHttpObject.provideApplicationContext(applicationContext)
        super.onCreate()
    }
}