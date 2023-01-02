package iak.currencyquote

import androidx.multidex.MultiDexApplication
import iak.currencyquote.di.SingletonAppObject
import iak.currencyquote.di.SingletonOkHttpObject

class RatesApp: MultiDexApplication() {
    override fun onCreate() {
        SingletonAppObject.provideApplicationContext(applicationContext)
        SingletonOkHttpObject.provideApplicationContext(applicationContext)
        super.onCreate()
    }
}