package dev.andrew.rates.ui.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.andrew.rates.di.SingletonAppObject
import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.source.CurrencySourceManager
import dev.andrew.rates.source.AppSettings
import dev.andrew.rates.ui.ActivityCurrency

class CurrencyViewModel(
    private val repositoryManager: CurrencySourceManager = SingletonAppObject.currencySourceManager,
    private val appSettings: AppSettings = SingletonAppObject.appSetting
): ViewModel() {
    private companion object {
        const val TAG = "ActivityCurrencyViewModel"
    }

    private lateinit var activity: ActivityCurrency

`    private lateinit var currencyCarriage: MutableLiveData<SharedExchangeViewModel.CurrencyCarriage>

    fun bindActivity(activity: ActivityCurrency) {
        this.activity = activity
    }

    fun bindCurrencyCarriage(currencyCarriage: MutableLiveData<SharedExchangeViewModel.CurrencyCarriage>) {
        this.currencyCarriage = currencyCarriage
    }

    fun getFirstCurrency() = appSettings.lastUsedCurrencyFirst

    fun getSecondCurrency() = appSettings.lastUsedCurrencySecond

    fun getFiatCurrencies() = repositoryManager.fiatCurrencyLive

    fun getCryptoCurrencies() = repositoryManager.cryptoCurrencyLive

    fun onCurrencyItemSelected(target: ICurrency, isChecked: Boolean): Boolean {
        activity.clearSearchQuery()

        if (isChecked) {
            when(currencyCarriage.value) {
                SharedExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    appSettings.lastUsedCurrencyFirst.value?.let { lastUsedCurrencyFirst ->
                        activity.clearCarriage(lastUsedCurrencyFirst)
                    }
                    appSettings.setLastUsedCurrencyFirst(target)
                    shiftCarriage()
                }
                SharedExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                    appSettings.lastUsedCurrencySecond.value?.let { lastUsedCurrencySecond ->
                        activity.clearCarriage(lastUsedCurrencySecond)
                    }
                    appSettings.setLastUsedCurrencySecond(target)
                    shiftCarriage()
                }
                null -> {
                }
            }
        } else {
            when(currencyCarriage.value) {
                SharedExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    val lastUsedCurrencySecond = appSettings.lastUsedCurrencySecond.value
                    if (lastUsedCurrencySecond?.name == target.name) {
                        shiftCarriage()
                    }

                }
                SharedExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                    val lastUsedCurrencyFirst = appSettings.lastUsedCurrencyFirst.value
                    if (lastUsedCurrencyFirst?.name == target.name) {
                        shiftCarriage()
                    }
                }
                null -> {
                }
            }
            activity.clearCarriage(target)
        }

        return false // returned not used
    }

    private fun shiftCarriage() {
        when(currencyCarriage.value) {
            SharedExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                currencyCarriage.value = SharedExchangeViewModel.CurrencyCarriage.SECONDARY
            }
            SharedExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                currencyCarriage.value = SharedExchangeViewModel.CurrencyCarriage.PRIMARY
            }
            else -> {}
        }
    }

    fun onCarriageChanged(carriage: SharedExchangeViewModel.CurrencyCarriage?) {
        when(carriage) {
            SharedExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                activity.updatePrimaryChip()
            }
            SharedExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                activity.updateSecondaryChip()
            }
            else -> {}
        }
    }

    fun onFirstCurrencyUpdated(it: ICurrency?) {
        if (it != null) {
            activity.setCarriage(it, SharedExchangeViewModel.CurrencyCarriage.PRIMARY)
        }
    }

    fun onSecondCurrencyUpdated(it: ICurrency?) {
        if (it != null) {
            activity.setCarriage(it, SharedExchangeViewModel.CurrencyCarriage.SECONDARY)
        }
    }
}