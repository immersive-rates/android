package iak.currencyquote.source.currency.not_impl

import iak.currencyquote.data.ICurrency
import iak.currencyquote.data.Rate
import iak.currencyquote.data.RateWithCurrency
import iak.currencyquote.source.currency.ICurrencySource
import iak.currencyquote.source.currency.RepositoryInfo
import iak.currencyquote.source.currency.cache.CacheSettings
import iak.currencyquote.source.currency.cache.ICachedCurrencySource


class CoinMarketCapRepository : ICurrencySource, ICachedCurrencySource {
    companion object {
    }

    override fun getInfo(): RepositoryInfo {
        TODO("Not yet implemented")
    }

    override fun getAvailableCurrencies(): List<ICurrency> {
        TODO("Not yet implemented")
    }

    override fun getLatestRate(base: ICurrency, to: ICurrency): Rate {
        TODO("Not yet implemented")
    }

    override fun getCacheSettings(): CacheSettings {
        TODO("Not yet implemented")
    }

    override fun getAllCurrenciesWitchRates(): List<RateWithCurrency> {
        TODO("Not yet implemented")
    }
}