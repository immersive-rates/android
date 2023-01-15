package dev.andrew.rates.ui.navigator

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import dev.andrew.rates.R
import dev.andrew.rates.ui.ActivityCurrency
import dev.andrew.rates.ui.ActivityExchange
import dev.andrew.rates.ui.ActivityRepositoryList

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