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


class MOEXRepository : ICurrencySource, ICachedCurrencySource {
    companion object {
        private const val API_BASE_URL = "https://iss.moex.com/iss"
        private const val API_CETS_CURR_PRESET_URL =
            "$API_BASE_URL/engines/currency/markets/selt/securities.json?iss.meta=off&iss.only=securities,marketdata&securities=CETS:USD000000TOD,CETS:USDRUB_TDB,CETS:USDRUB_TMS,CETS:USD000UTSTOM,CETS:USDRUB_SPT,FIXS:USDRUB_FIX0,WAPS:USDRUB_WAP0,CETS:EUR_RUB__TOD,CETS:EUR_RUB__TOM,CETS:EURRUB_SPT,CETS:EURUSD000TOD,CETS:EURUSD000TOM,CETS:EURUSD_SPT,CETS:CNY000000TOD,CETS:CNYRUB_TOM,CETS:CNYRUB_SPT,CETS:KZT000000TOM,CETS:BYNRUB_TOD,CETS:BYNRUB_TOM,CETS:HKDRUB_TOD,CETS:HKDRUB_TOM,CETS:GBPRUB_TOD,CETS:GBPRUB_TOM,CETS:TRYRUB_TOD,CETS:TRYRUB_TOM,CETS:CHFRUB_TOD,CETS:CHFRUB_TOM,CETS:JPYRUB_TOD,CETS:JPYRUB_TOM,CETS:USDJPY_TOD,CETS:USDJPY_TOM,CETS:USDJPY_SPT,CETS:GBPUSD_TOD,CETS:GBPUSD_TOM,CETS:USDCHF_TOD,CETS:USDCHF_TOM,CETS:USDCNY_TOD,CETS:USDCNY_TOM,CETS:USDKZT_TOD,CETS:USDKZT_TOM,CETS:USDTRY_TOD,CETS:USDTRY_TOM"
        private const val BASE_CURRENCY = "RUB"

        @JvmStatic
        val INFO = RepositoryInfo(
            labelRes = R.string.moex_label,
            shortLabelRes = R.string.short_moex_label,
            isSupportFiat = true,
            isSupportCrypto = false,
            homePageUrl = "https://www.moex.com/",
            updateInterval = RepositoryInfo.FIFE_SECONDS_INTERVAL
        )

        @JvmStatic
        private val CACHE_SETTINGS = CacheSettings(
            CacheSettings.FIFE_SECONDS_INTERVAL,
            CacheSettings.ONE_DAY_INTERVAL
        )

        const val TAG = "MOEXRepository"
    }

    private val client = SingletonOkHttpObject.buildCurrencyRepositoryHttpClient

    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun getInfo() = INFO

    class ColumsDataItem(
        val columns: List<String>? = null,
        val data: List<List<Any?>>? = null
    ) {
    }

    class MarketsSeltSecurities(
        val securities: ColumsDataItem? = null,
        val marketdata: ColumsDataItem? = null
    )

    private fun getCETSURL(): MarketsSeltSecurities? {
        val request = Request.Builder()
            .url(API_CETS_CURR_PRESET_URL)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0")
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val stream = body.byteStream()
                val data = objectMapper.readValue(stream, MarketsSeltSecurities::class.java)
                stream.close()
                return data
            }
            response.close()
        }
        return null
    }

    private fun exchangeCurrencies(from: String, to: String): Double {
        val data = getCETSURL()
        if (data != null) {
            val securitiesColumns = data.securities?.columns
            val securitiesData = data.securities?.data
            val marketColumns = data.marketdata?.columns
            val marketData = data.marketdata?.data

            var tomId: String? = null

            if (securitiesColumns != null
                && securitiesData != null
                && marketColumns != null
                && marketData != null) {
                val securIdColumnIndex = securitiesColumns.indexOf("SECID")
                val securFirstColumnIndex = securitiesColumns.indexOf("FACEUNIT")
                val securSecondColumnIndex = securitiesColumns.indexOf("CURRENCYID")
                val marketIdColumnIndex = marketColumns.indexOf("SECID")
                val marketRateColumnIndex = marketColumns.indexOf("LAST")

                if (securIdColumnIndex != -1
                    && securFirstColumnIndex != -1
                    && securSecondColumnIndex != -1
                    && marketIdColumnIndex != -1
                    && marketRateColumnIndex != -1) {

                    for (secData in securitiesData) {
                        val firstData = secData.getOrNull(securFirstColumnIndex)
                        if (firstData == from) {
                            val secondData = secData.getOrNull(securSecondColumnIndex)
                            if (secondData == to) {
                                val secId = secData.getOrNull(securIdColumnIndex)
                                if (secId is String) {

                                    if (secId.endsWith("TOD")) {
                                        for (marData in marketData) {
                                            if (marData.getOrNull(marketIdColumnIndex) == secId) {
                                                val rate = marData.getOrNull(marketRateColumnIndex)
                                                if (rate is Double) {
                                                    return rate
                                                }
                                            }
                                        }
                                    } else if (secId.endsWith("TOM")) {
                                        tomId = secId
                                    }
                                }
                            }
                        }
                    }

                    if (tomId != null) {
                        for (marData in marketData) {
                            if (marData.getOrNull(marketIdColumnIndex) == tomId) {
                                val rate = marData.getOrNull(marketRateColumnIndex)
                                if (rate is Double) {
                                    return rate
                                }
                            }
                        }
                    }
                }
            }
        }
        return .0
    }

    override fun getAvailableCurrencies(): List<ICurrency> {
        val data = getCETSURL()
        if (data != null) {
            val securitiesColumns = data.securities?.columns
            val securitiesData = data.securities?.data
            if (securitiesColumns != null
                && securitiesData != null) {
                val result = ArrayList<String>()
                var indexOfCurrencyColumn = securitiesColumns.indexOf("FACEUNIT")
                if (indexOfCurrencyColumn != -1)
                    for (datum in securitiesData) {
                        val columnData = datum.getOrNull(indexOfCurrencyColumn)
                        if (columnData is String) {
                            if (!result.contains(columnData)) {
                                result.add(columnData)
                            }
                        }
                    }
                indexOfCurrencyColumn =  securitiesColumns.indexOf("CURRENCYID")
                if (indexOfCurrencyColumn != -1)
                    for (datum in securitiesData) {
                        val columnData = datum.getOrNull(indexOfCurrencyColumn)
                        if (columnData is String) {
                            if (!result.contains(columnData)) {
                                result.add(columnData)
                            }
                        }
                    }
                return result.map { CurrencyBuilder.buildFiat(it, INFO.id) }
            }
        }
        return emptyList()
    }

    override fun getLatestRate(from: ICurrency, to: ICurrency): Rate {
        val fromName = from.name.uppercase()
        val toName = to.name.uppercase()

        val rate = if (fromName == BASE_CURRENCY) {
            exchangeCurrencies(toName, fromName).let {
                if (it > 0) {
                    1 / it
                } else it
            }
        } else {
            exchangeCurrencies(fromName, toName)
        }
        if (rate == .0) {
            throw CurrencyPairNotSupported(from, to)
        }
        return Rate(rate.toFloat())
    }

    override fun getCacheSettings() = CACHE_SETTINGS

    override fun getAllCurrenciesWitchRates(): List<RateWithCurrency> {
        return emptyList()
    }
}