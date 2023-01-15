package dev.andrew.rates.source.currency.cache

import dev.andrew.rates.data.RateWithCurrency

interface ICachedCurrencySource {
    fun getCacheSettings(): CacheSettings
    fun getAllCurrenciesWitchRates(): List<RateWithCurrency>
}