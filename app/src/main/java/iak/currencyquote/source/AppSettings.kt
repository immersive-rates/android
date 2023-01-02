package iak.currencyquote.source

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import iak.currencyquote.data.Crypto
import iak.currencyquote.data.CurrencyBuilder
import iak.currencyquote.data.Fiat
import iak.currencyquote.data.ICurrency
import iak.currencyquote.source.currency.CbRFRepository
import java.lang.Exception
import java.util.*

class AppSettings(
    private val preferences: SharedPreferences,
    private val userLocale: Locale
) {
    companion object {
        const val TAG = "UserSettings"

        private fun getCurrencyTypeDescriptor(baseCurrency: ICurrency): Int
                = if (baseCurrency is Fiat) 1 else if (baseCurrency is Crypto) 2 else -1

        private fun buildCurrencyFromDescriptor(name: String, sourceId: Int, descriptor: Int): ICurrency {
            return when(descriptor) {
                1 -> CurrencyBuilder.buildFiat(name, sourceId)
                2 -> CurrencyBuilder.buildCrypto(name, sourceId)
                else -> CurrencyBuilder.buildFiat(name, sourceId)
            }
        }

        private const val LAST_CURRENCY_NAME_KEY = "LAST_CURRENCY_NAME_KEY"
        private const val LAST_CURRENCY_SOURCE_ID_KEY = "LAST_CURRENCY_SOURCE_ID_KEY"
        private const val LAST_CURRENCY_TYPE_ID_KEY = "LAST_CURRENCY_TYPE_ID_KEY"
        private const val LAST_USED_SOURCE_ID_KEY = "LAST_USED_SOURCE_ID_KEY"

        @JvmStatic
        private val DEFAULT_REPOSITORY_INFO = CbRFRepository.INFO
    }

    private val lastUsedCurrencyFirst_MutableLiveData = MutableLiveData<ICurrency>(getLastUsedCurrencyFirstFromStorage())
    private val lastUsedCurrencySecond_MutableLiveData = MutableLiveData<ICurrency>(getLastUsedCurrencySecondFromStorage())

    val lastUsedCurrencyFirst: LiveData<ICurrency> = lastUsedCurrencyFirst_MutableLiveData
    val lastUsedCurrencySecond: LiveData<ICurrency> = lastUsedCurrencySecond_MutableLiveData

    var lastUsedSourceId: Int
    set(value) { preferences.edit().putInt(LAST_USED_SOURCE_ID_KEY, value).apply() }
    get() = preferences.getInt(LAST_USED_SOURCE_ID_KEY, DEFAULT_REPOSITORY_INFO.id)

    private fun putCurrencyByStoreIndex(currency: ICurrency, storeIndex: Int) {
        with(preferences.edit()) {
            putString(LAST_CURRENCY_NAME_KEY+storeIndex, currency.name)
            putInt(LAST_CURRENCY_SOURCE_ID_KEY+storeIndex, currency.sourceId)
            putInt(LAST_CURRENCY_SOURCE_ID_KEY+storeIndex, getCurrencyTypeDescriptor(currency))
            apply()
        }
    }

    private fun getCurrencyByStoreIndex(storeIndex: Int): ICurrency? {
        with(preferences) {
            val name = getString(LAST_CURRENCY_NAME_KEY+storeIndex, null)
            val sourceId = getInt(LAST_CURRENCY_SOURCE_ID_KEY+storeIndex, -1)
            val descriptorTypeId = getInt(LAST_CURRENCY_SOURCE_ID_KEY+storeIndex, -1)
            if (name == null || sourceId == -1 || descriptorTypeId == -1) {
                return null
            }
            return buildCurrencyFromDescriptor(name, sourceId, descriptorTypeId)
        }
    }

    private fun getLastUsedCurrencyFirstFromStorage(): ICurrency {
        try {
            return getCurrencyByStoreIndex(0)
                ?: CurrencyBuilder.buildFiat("EUR", CbRFRepository.INFO.id)
        } catch (e: Exception) {
            Log.d(TAG, "getLastUsedCurrencyFirstFromStorage", e)
            return CurrencyBuilder.buildFiat("EUR", CbRFRepository.INFO.id)
        }
    }

    private fun getLastUsedCurrencySecondFromStorage(): ICurrency {
        try {
            return getCurrencyByStoreIndex(1)
                ?: CurrencyBuilder.buildFiat("USD", CbRFRepository.INFO.id)
        } catch (e: Exception) {
            Log.d(TAG, "getLastUsedCurrencySecondFromStorage", e)
            return CurrencyBuilder.buildFiat("USD", CbRFRepository.INFO.id)
        }
    }

    @MainThread
    fun setLastUsedCurrencyFirst(currency: ICurrency) {
        putCurrencyByStoreIndex(currency, 0)
        lastUsedCurrencyFirst_MutableLiveData.value = currency
    }

    @MainThread
    fun setLastUsedCurrencySecond(currency: ICurrency) {
        putCurrencyByStoreIndex(currency, 1)
        lastUsedCurrencySecond_MutableLiveData.value = currency
    }

    @MainThread
    fun toggleFirstSecondCurrencies() {
        val second = lastUsedCurrencySecond.value
        val first = lastUsedCurrencyFirst.value
        second?.let { setLastUsedCurrencyFirst(it) }
        first?.let { setLastUsedCurrencySecond(it) }
    }
}