package dev.andrew.rates.source.currency

import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.data.Rate

interface ICurrencySource {
    @Deprecated("xyi")
    fun getInfo(): RepositoryInfo
    fun getAvailableCurrencies(): List<ICurrency>
    fun getLatestRate(base: ICurrency, to: ICurrency): Rate
}