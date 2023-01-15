package dev.andrew.rates.source.currency

import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import dev.andrew.rates.helper.CurrencyHelper
import dev.andrew.rates.R
import dev.andrew.rates.data.*
import dev.andrew.rates.di.SingletonOkHttpObject
import dev.andrew.rates.source.currency.cache.CacheSettings
import dev.andrew.rates.source.currency.cache.ICachedCurrencySource
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import okhttp3.Request
import org.json.JSONObject


class Fawazahmed0Repository : ICurrencySource, ICachedCurrencySource {
    companion object {
        const val TAG = "fawazahmed0Repository_Impl"

        private const val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1"
        private const val AVAILABLE_URL = "$BASE_URL/latest/currencies.min.json"
        private const val RATE_URL = "$BASE_URL/latest/currencies/%s/%s.min.json"
        private const val CURRENCY_RATES_URL = "$BASE_URL/latest/currencies/%s.min.json"

        private const val REPOSITORY_ID = 2

        val INFO = RepositoryInfo(
            id = REPOSITORY_ID,
            labelRes = R.string.fawazahmed0_label,
            shortLabelRes = R.string.short_fawazahmed0_label,
            isSupportFiat = true,
            isSupportCrypto = true,
            homePageUrl = "https://github.com/fawazahmed0/currency-api",
            updateInterval = RepositoryInfo.ONE_DAY_INTERVAL
        )
    }

    private class CurrencyMapReference : TypeReference<HashMap<String, String>>()
    private class CurrencyKeyMapReference : TypeReference<HashMap<String, Float>>()

    private val client = SingletonOkHttpObject.buildCurrencyRepositoryHttpClient
    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun getInfo() = INFO

    override fun getAvailableCurrencies(): List<ICurrency> {
        val request = Request.Builder()
            .url(AVAILABLE_URL)
            .get()
            .build()
        val response = client.newCall(request).execute()
        Log.d(TAG, "response=$response")
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val stream = body.byteStream()
                val currencyMap: HashMap<String, String> = objectMapper.readValue(stream, objectMapper.typeFactory.constructMapType(HashMap::class.java, String::class.java, String::class.java))
                stream.close()
                Log.d(TAG, "currencyList=$currencyMap")
                return currencyMap.keys.mapNotNull { name ->
                    if (name != null) {
                        if (CurrencyHelper.isFiat(name)) {
                            CurrencyBuilder.buildFiat(name, REPOSITORY_ID)
                        } else if (CurrencyHelper.isCrypto(name)) {
                            CurrencyBuilder.buildCrypto(name, REPOSITORY_ID)
                        } else null
                    } else null
                }
            }
        }
        return emptyList()
    }

    override fun getLatestRate(from: ICurrency, to: ICurrency): Rate {

        val fromName = from.name.lowercase()
        val toName = to.name.lowercase()

        val request = Request.Builder()
            .url(RATE_URL.format(fromName, toName))
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val jsonObject = JSONObject(body.string())
                val price = jsonObject.getDouble(toName).toFloat()
                if (price > 0f)
                    return Rate(price)
            }
        }

        throw CurrencyPairNotSupported(from, to)
    }

    override fun getCacheSettings() = CacheSettings(
        operationUpdateInterval = CacheSettings.ONE_DAY_INTERVAL,
        currenciesUpdateInterval = CacheSettings.ONE_DAY_INTERVAL,
    )

    override fun getAllCurrenciesWitchRates(): List<RateWithCurrency> {
        val availableCurrencies = getAvailableCurrencies()
        if (availableCurrencies.isNotEmpty()) {
            val result = ArrayList<RateWithCurrency>(availableCurrencies.size * availableCurrencies.size)
            for (currency in availableCurrencies) {
                val currencyName = currency.name
                val request = Request.Builder()
                    .url(CURRENCY_RATES_URL.format(currencyName))
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        val jsonObject = JSONObject(body.string())
                        val currencyKeyData = jsonObject.getJSONObject(currencyName)
                        val currencyMap: HashMap<String, Float> = objectMapper.readValue(currencyKeyData.toString(),
                            objectMapper.typeFactory.constructMapType(HashMap::class.java, String::class.java, Float::class.java))
                        for (currencyRate in currencyMap) {
                            result.add(
                                RateWithCurrency(
                                    currencyRate.component2(),
                                    currency,
                                    Fiat(currencyRate.component1(), REPOSITORY_ID))
                            )
                        }
                    }
                }
            }
            result.trimToSize()
            return result
        }
        return emptyList()
    }
}