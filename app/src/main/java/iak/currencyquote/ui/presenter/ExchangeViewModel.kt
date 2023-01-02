package iak.currencyquote.ui.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import iak.currencyquote.data.ExchangeOperation
import iak.currencyquote.data.ICurrency
import iak.currencyquote.di.SingletonAppObject
import iak.currencyquote.source.AppSettings
import iak.currencyquote.source.CurrencySourceManager
import iak.currencyquote.source.ExchangeHistorySettings
import iak.currencyquote.ui.ActivityExchange
import iak.currencyquote.ui.ExchangeHistoryAdapter

class ExchangeViewModel(
    private val appSettings: AppSettings = SingletonAppObject.appSetting,
    private val currencySourceManager: CurrencySourceManager = SingletonAppObject.currencySourceManager,
    private val exchangeHistory: ExchangeHistorySettings = SingletonAppObject.exchangeHistory,
): ViewModel() {

    private var activity: ActivityExchange? = null
    private lateinit var currencyCarriage: MutableLiveData<GlobalExchangeViewModel.CurrencyCarriage>
    private var onCurrencyCountInputBlock = false

    private lateinit var historyAdapter: ExchangeHistoryAdapter

    fun bindActivity(activity: ActivityExchange) {
        this.activity = activity
    }

    fun bindCurrencyCarriage(currencyCarriage: MutableLiveData<GlobalExchangeViewModel.CurrencyCarriage>) {
        this.currencyCarriage = currencyCarriage
    }

    fun bindAdapter(adapter: ExchangeHistoryAdapter) {
        historyAdapter = adapter
    }

    fun getFirstCurrency() = appSettings.lastUsedCurrencyFirst

    fun getSecondCurrency() = appSettings.lastUsedCurrencySecond

    fun onCurrencyInCountInput(text: String) {
        val value = text.toFloatOrNull()
        if (value != null) {
            if (!onCurrencyCountInputBlock) {
                onCurrencyCountInputBlock = true
                val secondCurrency = getSecondCurrency().value
                val firstCurrency = getFirstCurrency().value
                if (secondCurrency != null && firstCurrency != null) {
                    activity?.let { itActivity ->
                        currencySourceManager.exchange(
                            firstCurrency, secondCurrency, value
                        ).observe(itActivity) {
                            if (it > 0) {
                                addToExchangeHistory(firstCurrency, secondCurrency, value, it)
                                activity?.setOutCurrencyValue(it.toString())
                            }
                            onCurrencyCountInputBlock = false
                        }
                    }
                }
            }
        }
    }

    fun onCurrencyOutputCountInput(text: String) {
        val value = text.toFloatOrNull()
        if (value != null) {
            if (!onCurrencyCountInputBlock) {
                onCurrencyCountInputBlock = true
                val secondCurrency = getSecondCurrency().value
                val firstCurrency = getFirstCurrency().value
                if (secondCurrency != null && firstCurrency != null) {
                    activity?.let { itActivity ->
                        currencySourceManager.exchange(
                            secondCurrency, firstCurrency, value
                        ).observe(itActivity) {
                            if (it > 0) {
                                addToExchangeHistory(secondCurrency, firstCurrency, value, it)
                                activity?.setInCurrencyValue(it.toString())
                            }
                            onCurrencyCountInputBlock = false
                        }
                    }
                }
            }
        }
    }

    private fun addToExchangeHistory(
        fromCurrency: ICurrency,
        toCurrency: ICurrency,
        fromValue: Float,
        toValue: Float
    ) {
        exchangeHistory.addOperation(ExchangeOperation(
            repositoryInfo = currencySourceManager.getInfo(),
            fromName = fromCurrency.name,
            fromCount = fromValue,
            toName = toCurrency.name,
            toCount = toValue
        ))
    }

    fun onCurrencyToggleTap() {
        appSettings.toggleFirstSecondCurrencies()
    }

    fun getExchangeHistory() = exchangeHistory.getLastOperations()

    fun onHistoryItemSwiped(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        historyAdapter.removeItemAt(position)
        exchangeHistory.removeOperationAt(position)
    }

    fun onPrimarySelected() {
        currencyCarriage.value = GlobalExchangeViewModel.CurrencyCarriage.PRIMARY
    }

    fun onSecondSelected() {
        currencyCarriage.value = GlobalExchangeViewModel.CurrencyCarriage.SECONDARY
    }

    fun onInputFocusChange(hasFocus: Boolean) {
        if (hasFocus) {
            onPrimarySelected()
        }
    }

    fun onOutputFocusChange(hasFocus: Boolean) {
        if (hasFocus) {
            onSecondSelected()
        }
    }
}