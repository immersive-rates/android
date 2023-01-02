package iak.currencyquote.ui.presenter

import android.util.Log
import androidx.lifecycle.ViewModel
import iak.currencyquote.di.SingletonAppObject
import iak.currencyquote.source.CurrencySourceManager
import iak.currencyquote.source.currency.RepositoryInfo
import iak.currencyquote.source.currency.ICurrencySource
import iak.currencyquote.ui.RepositoryListAdapter

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