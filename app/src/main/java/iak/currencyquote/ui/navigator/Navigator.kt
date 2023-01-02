package iak.currencyquote.ui.navigator

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import iak.currencyquote.R
import iak.currencyquote.ui.ActivityCurrency
import iak.currencyquote.ui.ActivityExchange
import iak.currencyquote.ui.ActivityRepositoryList

class Navigator(
    private val fragmentManager: FragmentManager
) {
    fun toExchange() {
        fragmentManager.commit {
            replace<ActivityExchange>(R.id.primary_container,
                tag = ActivityExchange.TAG)
        }
    }
    fun toCurrencies() {
        fragmentManager.beginTransaction()
            .replace(R.id.primary_container,
                ActivityCurrency.newInstance(), ActivityCurrency.TAG)
            .addToBackStack(ActivityCurrency.TAG)
            .commit()
    }
    fun toConnections() {
        fragmentManager.beginTransaction()
            .replace(R.id.primary_container,
                ActivityRepositoryList.newInstance(), ActivityRepositoryList.TAG)
            .addToBackStack(ActivityRepositoryList.TAG)
            .commit()
    }
    fun toHistory() {
    }
    fun toFeatures() {
    }
    fun toFeedback() {
    }
    fun toRateApp() {
    }
}