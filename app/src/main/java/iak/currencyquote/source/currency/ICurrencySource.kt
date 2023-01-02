package iak.currencyquote.source.currency

import iak.currencyquote.data.ICurrency
import iak.currencyquote.data.Rate

interface ICurrencySource {
    @Deprecated("xyi")
    fun getInfo(): RepositoryInfo
    fun getAvailableCurrencies(): List<ICurrency>
    fun getLatestRate(base: ICurrency, to: ICurrency): Rate
}