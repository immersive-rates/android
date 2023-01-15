package dev.andrew.rates.source.currency.cache

import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.data.Rate
import dev.andrew.rates.data.RateWithCurrency
import dev.andrew.rates.source.currency.ICurrencySource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

    class CachedCurrencyRepositoryWrapper<T>(
    private val sourceToCache: T
): ICurrencySource where T: ICurrencySource, T: ICachedCurrencySource {
    private var cachedOperations: ArrayList<RateWithCurrency> = ArrayList()
    private val cachedCurrencies: List<ICurrency> = emptyList()

    private var isAllowedUseCache: Boolean = false

    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        if (isAllowedUseCache) {
            coroutineScope.launch {
                cachedOperations = ArrayList(
                    sourceToCache.getAllCurrenciesWitchRates()
                )
            }
        }
    }

    private fun getCachedDataByExchanged(exchanged: ICurrency, resulting: ICurrency): RateWithCurrency? {
        return cachedOperations.find {
            it.exchange.name == exchanged.name
            && it.resulting.name == resulting.name
        }
    }

    private fun getCachedAvailableCurrencies(): List<ICurrency>? {
        return if (cachedCurrencies.isNotEmpty()) {
            cachedCurrencies
        } else {
            null
        }
    }

    private fun getCachedLatestRate(exchanged: ICurrency, resulting: ICurrency): Rate? {
        val cached = getCachedDataByExchanged(exchanged, resulting)
        if (cached != null) {
            return Rate(
                cached.rate
            )
        }
        return null
    }

    override fun getInfo() = sourceToCache.getInfo()

    override fun getAvailableCurrencies(): List<ICurrency> {
        if (isAllowedUseCache) {
            getCachedAvailableCurrencies()?.also {
                return it
            }
        }
        return sourceToCache.getAvailableCurrencies()
    }

    override fun getLatestRate(base: ICurrency, to: ICurrency): Rate {
        if (isAllowedUseCache) {
            getCachedLatestRate(base, to)?.also {
                return it
            }
        }
        return sourceToCache.getLatestRate(base, to)
    }
}