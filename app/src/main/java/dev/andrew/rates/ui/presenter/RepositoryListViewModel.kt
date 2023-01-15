package dev.andrew.rates.ui.presenter

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.andrew.rates.di.SingletonAppObject
import dev.andrew.rates.source.CurrencySourceManager
import dev.andrew.rates.source.currency.RepositoryInfo
import dev.andrew.rates.source.currency.ICurrencySource
import dev.andrew.rates.ui.adapter.RepositoryListAdapter

class RepositoryListViewModel(
    private val repositoryManager: CurrencySourceManager = SingletonAppObject.currencySourceManager,
) : ViewModel() {

    private lateinit var adapter: RepositoryListAdapter

    private var currentSourceIsMark = false

    fun bindAdapter(adapter: RepositoryListAdapter) {
        currentSourceIsMark = false
        this.adapter = adapter
    }

    fun getInfos(): List<RepositoryInfo> = repositoryManager.getInfoList()

    fun onItemChecked(repositoryInfo: RepositoryInfo, isChecked: Boolean) {
        Log.d("ViewModel", "$repositoryInfo | $isChecked")
        if (adapter.getSelected() == repositoryInfo) {
            if (!isChecked)
                adapter.setChecked(repositoryInfo, true)
        } else {
            if (isChecked) {
                repositoryManager.setCurrentRepository(repositoryInfo)
                adapter.unselectAll()
                adapter.setChecked(repositoryInfo, true)
            }
        }
    }

    fun getCurrentRepository() = repositoryManager.currentRepositoryLive

    fun onCurrencySourceChanged(source: ICurrencySource) {
        if (!currentSourceIsMark) {
            currentSourceIsMark = true
            onItemChecked(source.getInfo(), true)
        }
    }
}