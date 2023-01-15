package dev.andrew.rates.source.currency

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dev.andrew.rates.R
import dev.andrew.rates.data.*
import dev.andrew.rates.di.SingletonOkHttpObject
import dev.andrew.rates.source.currency.cache.CacheSettings
import dev.andrew.rates.source.currency.cache.ICachedCurrencySource
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import okhttp3.Request


class BinanceRepository: ICurrencySource, ICachedCurrencySource, IPingableSource {
    companion object {
        const val TAG = "BinanceRepository_Impl"

        private const val HOME_URL = "https://www.binance.com/en"
        private const val BASE_URL = "https://api.binance.com"
        private const val AVAILABLE_URL = "$BASE_URL/api/v3/exchangeInfo"
        private const val PRICE_URL = "$BASE_URL/api/v3/ticker/price?symbol="
        private const val PRICE_ALL_URL = "$BASE_URL/api/v3/ticker/price"
        private const val PING_PONG_URL = "$BASE_URL/api/v3/ping"

        private const val REPOSITORY_ID = 1
        val INFO = RepositoryInfo(
            labelRes = R.string.binance_label,
            shortLabelRes = R.string.short_binance_label,
            isSupportFiat = false,
            isSupportCrypto = true,
            homePageUrl = HOME_URL,
            updateInterval = RepositoryInfo.ONE_SECOND_INTERVAL
        )
    }

    private class ExchangeInfoSymbol(
        val baseAsset: String = ""
    )

    private class ExchangeInfoSymbols(
        val symbols: List<ExchangeInfoSymbol> = emptyList()
    )

    private class TickerPriceBy(
        val price: Float = 0f,
        val name: String = ""
    )

    private class TickerPrice(
        val price: Float = 0f,
        val symbol: String = ""
    )

    private val client = SingletonOkHttpObject.buildCurrencyRepositoryHttpClient
    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun getInfo() = INFO

    override fun getAvailableCurrencies(): List<Crypto> {
        val request = Request.Builder()
            .url(AVAILABLE_URL)
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val stream = body.byteStream()
                val info = objectMapper.readValue(stream, ExchangeInfoSymbols::class.java)
                stream.close()
                val result = ArrayList<String>(info.symbols.size / 4)
                for (symbol in info.symbols) {
                    if (!result.contains(symbol.baseAsset)) {
                        result.add(symbol.baseAsset)
                    }
                }
                return result.map {
                    CurrencyBuilder.buildCrypto(it, REPOSITORY_ID) }
            }
        }
        return emptyList()
    }

    override fun getLatestRate(from: ICurrency, to: ICurrency): Rate {
        val request = Request.Builder()
            .url(PRICE_URL + from.name + to.name)
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val stream = body.byteStream()
                val price = objectMapper.readValue(stream, TickerPriceBy::class.java)
                stream.close()
                if (price.price > 0)
                    return Rate(price.price)
            }
            response.close()
        }
        throw CurrencyPairNotSupported(from, to)
    }

    override fun getCacheSettings() = CacheSettings(
        operationUpdateInterval = CacheSettings.ONE_SECOND_INTERVAL,
        currenciesUpdateInterval = CacheSettings.ONE_DAY_INTERVAL,
    )

    override fun getAllCurrenciesWitchRates(): List<RateWithCurrency> {
        val currencies = getAvailableCurrencies()
        if (currencies.isNotEmpty()) {
            val request = Request.Builder()
                .url(PRICE_ALL_URL)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body
                if (body != null) {
                    val stream = body.byteStream()
                    val priceList: Array<TickerPrice> = objectMapper.readValue(stream,
                        objectMapper.typeFactory.constructArrayType(TickerPrice::class.java)
                    )
                    stream.close()
                    val result = ArrayList<RateWithCurrency>(priceList.size)
                    for (exchangeCurrency in currencies) {
                        val currencyExchangeName = exchangeCurrency.name
                        for (tickerPriceBy in priceList) {
                            val tickerPriceSymbol = tickerPriceBy.symbol
                            if (tickerPriceSymbol.startsWith(exchangeCurrency.name)) {
                                for (resultCurrency in currencies) {
                                    val resultCurrencyName = resultCurrency.name
                                    if (currencyExchangeName + resultCurrencyName == tickerPriceSymbol) {
                                        result.add(
                                            RateWithCurrency(
                                                tickerPriceBy.price,
                                                exchangeCurrency,
                                                CurrencyBuilder.buildCrypto(resultCurrencyName, REPOSITORY_ID)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    result.trimToSize()
                    return result
                }
            }
        }
        return emptyList()
    }

    override fun pingPong(): Long {
        val request = Request.Builder()
            .url(PING_PONG_URL)
            .get()
            .build()
        val response = client.newCall(request).execute()
        return response.receivedResponseAtMillis - response.sentRequestAtMillis
    }
}