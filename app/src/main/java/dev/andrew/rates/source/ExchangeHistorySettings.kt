package dev.andrew.rates.source

import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.andrew.rates.data.ExchangeOperation
import org.json.JSONObject

/*
* todo избавиться от реализации с Set т.к. порядок не соблюдается
*/
class ExchangeHistorySettings(
    private val preferences: SharedPreferences,
    private val currencySourceManager: CurrencySourceManager,
) {
    companion object {
        const val STRING_SET_KEY = "HISTORY_STRING_SET_KEY"
    }

    private var historyList = getMappedHistoryFromStorage().toMutableList()

    private val mutableLiveDataHistory: MutableLiveData<List<ExchangeOperation>> by lazy {
        MutableLiveData(historyList)
    }

    private fun mapToStorage(exchange: ExchangeOperation): String? {
        return try {
            JSONObject().apply {
                put("sourceId", exchange.repositoryInfo.id)
                put("fromName", exchange.fromName)
                put("fromCount", exchange.fromCount)
                put("toName", exchange.toName)
                put("toCount", exchange.toCount)
                put("at", exchange.at)
            }.toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun mapFromStorage(value: String): ExchangeOperation? {
        return try {
            JSONObject(value).let {
                ExchangeOperation(
                    currencySourceManager.getInfo(it.getInt("sourceId")),
                    it.getString("fromName"),
                    it.getDouble("fromCount").toFloat(),
                    it.getString("toName"),
                    it.getDouble("toCount").toFloat(),
                    it.getLong("at")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getHistoryFromStorage() =
        try { preferences.getStringSet(STRING_SET_KEY, emptySet<String>())!! }
        catch (e: Exception) { emptySet<String>() }

    private fun setHistoryToStorage(storageSet: Set<String>) {
        with(preferences.edit()) {
            putStringSet(STRING_SET_KEY, storageSet)
            apply()
        }
    }

    private fun getMappedHistoryFromStorage(): List<ExchangeOperation> {
        val storageSet = getHistoryFromStorage()
        if (storageSet.isEmpty())
            return emptyList()
        return storageSet.mapNotNull { mapFromStorage(it) }.sortedByDescending { it.at }
    }

    private fun removeStoredOperation(exchange: ExchangeOperation) {
        val mappedOperation = mapToStorage(exchange)
        val fromStorage = getHistoryFromStorage()
        val toStorage = fromStorage.toMutableList()
        toStorage.removeIf { it == mappedOperation }
        setHistoryToStorage(toStorage.toSet())
    }

    private fun addOperationToStore(exchange: ExchangeOperation) {
        val mappedOperation = mapToStorage(exchange)
        val storageList = getHistoryFromStorage().toMutableList()
        storageList.add(0, mappedOperation)
        setHistoryToStorage(storageList.toSet())
    }

    private fun removeFromCache(index: Int) {
        historyList.removeAt(index)
    }

    private fun addToCache(exchange: ExchangeOperation) {
        historyList.add(0, exchange)
    }

    private fun updateLive() {
        mutableLiveDataHistory.value = historyList
    }

    @MainThread
    fun removeOperationAt(index: Int) {
        if (historyList.isNotEmpty() && historyList.size >= index) {
            removeStoredOperation(historyList[index])
            removeFromCache(index)
            updateLive()
        }
    }

    @MainThread
    fun addOperation(exchange: ExchangeOperation) {
        for (index in historyList.indices) {
            val iOperation = historyList[index]
            if (iOperation.fromCount == iOperation.fromCount
                && iOperation.toCount == exchange.toCount) {
                removeStoredOperation(iOperation)
                removeFromCache(index)
                break
            }
        }

        addOperationToStore(exchange)
        addToCache(exchange)
        updateLive()
    }

    fun getLastOperations(): LiveData<List<ExchangeOperation>> = mutableLiveDataHistory
}