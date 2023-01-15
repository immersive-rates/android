package dev.andrew.rates.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import dev.andrew.rates.ConnectStatusManager
import dev.andrew.rates.R
import dev.andrew.rates.databinding.ActivityMainBinding
import dev.andrew.rates.di.SingletonAppObject
import dev.andrew.rates.helper.EMailHelper
import dev.andrew.rates.source.CurrencySourceManager
import dev.andrew.rates.source.RepositoryConnectionStatus
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import dev.andrew.rates.ui.navigator.Navigator
import dev.andrew.rates.ui.presenter.GlobalExchangeViewModel

class MainActivity(
    private val repositoryConnectionStatus: RepositoryConnectionStatus = SingletonAppObject.repositoryConnectionStatus,
    private val connectStatusManager: ConnectStatusManager = SingletonAppObject.connectStatusManager,
    private val currencySourceManager: CurrencySourceManager = SingletonAppObject.currencySourceManager,
) : AppCompatActivity(), ActivityExchange.ExchangerHandler {
    companion object {
        const val TAG = "MainActivity"
    }

    private val globalActivityViewModel: GlobalExchangeViewModel by viewModels()

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

        Navigator(supportFragmentManager).toExchange()

//        binding.toolbar.setOnMenuItemClickListener {
//            return@setOnMenuItemClickListener when(it.itemId) {
//                R.id.connections -> {
//                    navigateToRepositoryList()
//                    true
//                }
//                R.id.email_feedback -> {
//                    openEMailIntent()
//                    true
//                }
//                else -> {
//                    false
//                }
//            }
//        }

//        lifecycleScope.launch {
//            HumanisedTimeHelper.humanisedTimeFlow(
//                Calendar.getInstance(TimeZone.GMT_ZONE).timeInMillis
//            ).flowWithLifecycle(lifecycle).collect {
//                    binding.toolbar.subtitle = "Cached at $it"
//                }
//        }

        globalActivityViewModel.onFromCurrencyTap.observe(this) {
            navigateToCurrencyFragment()
        }

        globalActivityViewModel.onToCurrencyTap.observe(this) {
            navigateToCurrencyFragment()
        }

        connectStatusManager.isConnect.observe(this) {
            binding.noConnectionBanner.isVisible = !it
        }

        repositoryConnectionStatus.statusLive.observe(this) {
            when(it) {
                RepositoryConnectionStatus.Status.IDLE -> {
                    binding.toolbar.title = getString(R.string.app_name)
                }
                RepositoryConnectionStatus.Status.UPDATING_DONE -> {
                    binding.toolbar.title = getString(R.string.app_name)
                }
                else -> {
                    binding.toolbar.title = it.name
                }
            }
        }

        currencySourceManager.lastRuntimeException.observe(this) {
            if (it is CurrencyPairNotSupported) {
//                Toast.makeText(this, getString(R.string.currency_pair_not_supported_by_repository,
//                    getString(currencySourceManager.currentRepositoryLive.value?.getInfo()?.labelRes ?: 0),
//                    it.first.name,
//                    it.second.name
//                ), Toast.LENGTH_LONG).show()
                binding.repositoryErrorBanner.text = getString(R.string.currency_pair_not_supported_by_repository,
                    getString(currencySourceManager.currentRepositoryLive.value?.getInfo()?.labelRes ?: 0),
                    it.first.name,
                    it.second.name
                )
                binding.repositoryErrorBanner.isVisible = true
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