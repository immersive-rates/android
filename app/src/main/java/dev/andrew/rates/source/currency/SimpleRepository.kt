package dev.andrew.rates.source.currency

import dev.andrew.rates.R
import dev.andrew.rates.data.CurrencyBuilder
import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.data.Rate
import dev.andrew.rates.source.exception.CurrencyPairNotSupported

class SimpleRepository : ICurrencySource {
    companion object {
        val INFO = RepositoryInfo(
            labelRes = R.string.debug_label,
            shortLabelRes = R.string.short_debug_label,
            isSupportFiat = true,
            isSupportCrypto = false,
            homePageUrl = "",
            updateInterval = RepositoryInfo.ONE_DAY_INTERVAL
        )

        @JvmStatic
        private val CURRENCIES = arrayListOf(
            CurrencyBuilder.buildFiat("EUR", 0),
            CurrencyBuilder.buildFiat("USD", 0),
            CurrencyBuilder.buildFiat("RUB", 0),
            CurrencyBuilder.buildFiat("CNY", 0),
            CurrencyBuilder.buildFiat("EURUSD", 0),
            CurrencyBuilder.buildFiat("ADENA", 0)
        )
    }

    override fun getInfo() = INFO

    override fun getAvailableCurrencies() = CURRENCIES

    override fun getLatestRate(base: ICurrency, to: ICurrency): Rate {
        when(base.name) {
            "EURUSD" -> {
                when(to.name) {
                    "ADENA" -> return Rate(47619047619f)
                }
            }
            "ADENA" -> {
                when(to.name) {
                    "EURUSD" -> return Rate(476190476.19f)
                }
            }
        }
        throw CurrencyPairNotSupported(base, to)
    }
}