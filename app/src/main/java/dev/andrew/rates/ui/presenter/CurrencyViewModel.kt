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

    private lateinit var currencyCarriage: MutableLiveData<GlobalExchangeViewModel.CurrencyCarriage>

    fun bindActivity(activity: ActivityCurrency) {
        this.activity = activity
    }

    fun bindCurrencyCarriage(currencyCarriage: MutableLiveData<GlobalExchangeViewModel.CurrencyCarriage>) {
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
                GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    appSettings.lastUsedCurrencyFirst.value?.let { lastUsedCurrencyFirst ->
                        activity.clearCarriage(lastUsedCurrencyFirst)
                    }
                    appSettings.setLastUsedCurrencyFirst(target)
                    shiftCarriage()
                }
                GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
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
                GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    val lastUsedCurrencySecond = appSettings.lastUsedCurrencySecond.value
                    if (lastUsedCurrencySecond?.name == target.name) {
                        shiftCarriage()
                    }

                }
                GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
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
            GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                currencyCarriage.value = GlobalExchangeViewModel.CurrencyCarriage.SECONDARY
            }
            GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                currencyCarriage.value = GlobalExchangeViewModel.CurrencyCarriage.PRIMARY
            }
            else -> {}
        }
    }

    fun onCarriageChanged(carriage: GlobalExchangeViewModel.CurrencyCarriage?) {
        when(carriage) {
            GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                activity.updatePrimaryChip()
            }
            GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                activity.updateSecondaryChip()
            }
            else -> {}
        }
    }

    fun onFirstCurrencyUpdated(it: ICurrency?) {
        if (it != null) {
            activity.setCarriage(it, GlobalExchangeViewModel.CurrencyCarriage.PRIMARY)
        }
    }

    fun onSecondCurrencyUpdated(it: ICurrency?) {
        if (it != null) {
            activity.setCarriage(it, GlobalExchangeViewModel.CurrencyCarriage.SECONDARY)
        }
    }
}