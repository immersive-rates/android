package dev.andrew.rates.source.currency

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import dev.andrew.rates.R
import dev.andrew.rates.data.*
import dev.andrew.rates.di.SingletonOkHttpObject
import dev.andrew.rates.source.currency.cache.CacheSettings
import dev.andrew.rates.source.currency.cache.ICachedCurrencySource
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class RussianCentralBankRepository : ICurrencySource, ICachedCurrencySource  {
    companion object {
        const val TAG = "CbRFRepository_Impl"

        private const val BASE_URL = "https://www.cbr.ru/scripts"
        private const val AVAILABLE_URL = "$BASE_URL/XML_val.asp?d=0"
        private const val RATE_URL = "$BASE_URL/XML_daily.asp"

        private const val REPOSITORY_ID = 0

        val INFO = RepositoryInfo(
            id = REPOSITORY_ID,
            labelRes = R.string.cbrf_label,
            shortLabelRes = R.string.short_cbrf_label,
            isSupportFiat = true,
            isSupportCrypto = false,
            homePageUrl = BASE_URL,
            updateInterval = RepositoryInfo.ONE_DAY_INTERVAL
        )
    }

    private inner class ValCurs(
        val ValCurs: ArrayList<Valute>,
        val DateAttr: String
    )

    private inner class Valute(
        val CharCode: String = "",
        val Nominal: Int = 0,
        val Value: String = "",
    )

    private val client = SingletonOkHttpObject.buildCurrencyRepositoryHttpClient

    private var xmlPullParserFactory = XmlPullParserFactory.newInstance()
    private var parser = xmlPullParserFactory.newPullParser().apply {
        setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    }

    private var lastValCurs: ValCurs? = null

    private val locale = Locale("ru")
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

    private fun requestValCurs(): List<Valute> {
        Log.d(TAG, "requestValCurs")
        lastValCurs?.let { lastValCurs ->
            if (lastValCurs.ValCurs.isNotEmpty()
                && lastValCurs.DateAttr.isNotEmpty()) {
                val calendar = Calendar.getInstance(locale)
                val formattedNow = simpleDateFormat.format(calendar)
                if (formattedNow == lastValCurs.DateAttr) {
                    return lastValCurs.ValCurs
                }
            }
        }
        val request = Request.Builder()
            .url(RATE_URL)
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body
            if (body != null) {
                val stream = body.byteStream()

                val valuteList = ArrayList<Valute>(36)
                valuteList.add(Valute("RUB", 1, "1"))

                var DateAttr: String = ""

                var CharCode: String = ""
                var Nominal: Int = 0
                var Value: String = ""

                parser.setInput(stream, null)
                var tag: String? = ""
                var tagContent = ""
                var event = parser.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    tag = parser.name
                    when (event) {
                        XmlPullParser.START_TAG -> {
                            when (tag) {
                                "ValCurs" -> {
                                    try {
                                        DateAttr = parser.getAttributeValue(null, "Date")
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                        XmlPullParser.TEXT -> tagContent = parser.text
                        XmlPullParser.END_TAG -> {
                            when (tag) {
                                "CharCode" -> {
                                    CharCode = tagContent
                                }
                                "Nominal" -> {
                                    Nominal = tagContent.toInt()
                                }
                                "Value" -> {
                                    Value = tagContent
                                    valuteList.add(Valute(
                                        CharCode = CharCode,
                                        Nominal = Nominal,
                                        Value = Value))
                                }
                            }
                        }
                    }
                    event = parser.next()
                }
                stream.close()
                val valCurs = ValCurs(
                    ValCurs = valuteList,
                    DateAttr = DateAttr
                ).also {
                    lastValCurs = it
                }
                return valCurs.ValCurs
            }
        }
        return emptyList()
    }

    private fun calcRate(fromCurs: Valute, toCurs: Valute) =
    fromCurs.Value.replace(',', '.').toFloat() / toCurs.Value.replace(',', '.').toFloat() * toCurs.Nominal

    override fun getInfo() = INFO

    override fun getAvailableCurrencies(): List<Fiat> {
        return requestValCurs().map {
            CurrencyBuilder.buildFiat(it.CharCode, REPOSITORY_ID)
        }
    }

    override fun getLatestRate(from: ICurrency, to: ICurrency): Rate {
        val valCurs = requestValCurs()
        Log.d(TAG, "valCurs size ${valCurs.size}")

        val fromName = from.name.uppercase()
        val toName = to.name.uppercase()

        val fromCurs = valCurs.find {
            it.CharCode == fromName
        }
        val toCurs = valCurs.find {
            it.CharCode == toName
        }
        Log.d(TAG, "fromCurs $fromCurs, toCurs $toCurs")
        if (fromCurs != null && toCurs != null) {
            val calc = calcRate(fromCurs, toCurs)
            if (calc > 0f)
                return Rate(calc)
        }

        throw CurrencyPairNotSupported(from, to)
    }

    override fun getCacheSettings() = CacheSettings(
        operationUpdateInterval = CacheSettings.ONE_DAY_INTERVAL,
        currenciesUpdateInterval = CacheSettings.ONE_DAY_INTERVAL,
    )

    override fun getAllCurrenciesWitchRates(): List<RateWithCurrency> {
        val valCurs = requestValCurs()
        if (valCurs.isNotEmpty()) {
            val result = ArrayList<RateWithCurrency>(valCurs.size*valCurs.size)
            for (fromCurs in valCurs) {
                val fromCurrency = CurrencyBuilder.buildFiat(fromCurs.CharCode, REPOSITORY_ID)
                for (toCurs in valCurs) {
                    result.add(
                        RateWithCurrency(
                            calcRate(fromCurs, toCurs),
                            fromCurrency,
                            CurrencyBuilder.buildFiat(toCurs.CharCode, REPOSITORY_ID)
                        )
                    )
                }
            }
            result.trimToSize()
            return result
        }
        return emptyList()
    }
}