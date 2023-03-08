package dev.andrew.rates.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dev.andrew.rates.ConnectStatusManager
import dev.andrew.rates.R
import dev.andrew.rates.databinding.ActivityMainBinding
import dev.andrew.rates.di.SingletonAppObject
import dev.andrew.rates.helper.EMailHelper
import dev.andrew.rates.source.CurrencySourceManager
import dev.andrew.rates.source.RepositoryConnectionStatus
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import dev.andrew.rates.ui.navigator.Navigator
import dev.andrew.rates.ui.presenter.SharedExchangeViewModel

class MainActivity(
    private val repositoryConnectionStatus: RepositoryConnectionStatus = SingletonAppObject.repositoryConnectionStatus,
    private val connectStatusManager: ConnectStatusManager = SingletonAppObject.connectStatusManager,
    private val currencySourceManager: CurrencySourceManager = SingletonAppObject.currencySourceManager,
) : AppCompatActivity(), ActivityExchange.ExchangerHandler {
    companion object {
        const val TAG = "MainActivity"
    }

    private val sharedActivityViewModel: SharedExchangeViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.connections -> {
                        navigateToRepositoryList()
                        true
                    }
                    R.id.email_feedback -> {
                        EMailHelper.navToEmailCompose(this@MainActivity, "it.roger@outlook.com")
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        })

        if (savedInstanceState == null) {
            Navigator(supportFragmentManager).toExchange()
            checkAndPlayUpdates()
        }


        sharedActivityViewModel.onFromCurrencyTap.observe(this) {
            navigateToCurrencyFragment()
        }

        sharedActivityViewModel.onToCurrencyTap.observe(this) {
            navigateToCurrencyFragment()
        }

        connectStatusManager.isConnect.observe(this) {
            binding.noConnectionBanner.isVisible = !it
        }

        repositoryConnectionStatus.statusLive.observe(this) {
            binding.toolbar.title = when(it) {
                RepositoryConnectionStatus.Status.IDLE -> getString(R.string.app_name)
                RepositoryConnectionStatus.Status.CONNECTION_INPROGRESS -> getString(R.string.repo_conn_status_progress)
                RepositoryConnectionStatus.Status.CONNECTION_FAIL -> getString(R.string.repo_conn_status_fail)
                RepositoryConnectionStatus.Status.UPDATING_INPROGRESS -> getString(R.string.repo_upd_status_progress)
                RepositoryConnectionStatus.Status.UPDATING_DONE -> getString(R.string.app_name)
                RepositoryConnectionStatus.Status.UPDATING_FAIL -> getString(R.string.repo_upd_status_fail)
            }
        }

        currencySourceManager.lastRuntimeException.observe(this) {
            if (it is CurrencyPairNotSupported) {
                Toast.makeText(this, getString(R.string.currency_pair_not_supported_by_repository,
                    getString(currencySourceManager.currentRepositoryLive.value?.getInfo()?.labelRes ?: 0),
                    it.first.name,
                    it.second.name
                ), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAndPlayUpdates() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                appUpdateManager.startUpdateFlow(appUpdateInfo, this,
                    AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE))
            }
        }
    }

    private fun navigateToRepositoryList() {
        Navigator(supportFragmentManager).toConnections()
    }

    private fun navigateToCurrencyFragment() {
        Navigator(supportFragmentManager).toCurrencies()
    }

    override fun onPrimarySelected() {
        navigateToCurrencyFragment()
    }

    override fun onSecondSelected() {
        navigateToCurrencyFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}