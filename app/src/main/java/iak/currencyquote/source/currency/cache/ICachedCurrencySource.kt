package iak.currencyquote.source.currency.cache

import iak.currencyquote.data.RateWithCurrency

interface ICachedCurrencySource {
    fun getCacheSettings(): CacheSettings
    fun getAllCurrenciesWitchRates(): List<RateWithCurrency>
}