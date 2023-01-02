package iak.currencyquote.ui.presenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalExchangeViewModel: ViewModel() {
    enum class CurrencyCarriage {
        PRIMARY,
        SECONDARY
    }
    val onFromCurrencyTap = MutableLiveData<Void?>()
    val onToCurrencyTap = MutableLiveData<Void?>()
    val currencyCarriage = MutableLiveData(CurrencyCarriage.PRIMARY)
}