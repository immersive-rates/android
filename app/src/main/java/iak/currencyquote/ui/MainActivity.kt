package iak.currencyquote.ui

import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import iak.currencyquote.ConnectStatusManager
import iak.currencyquote.R
import iak.currencyquote.databinding.ActivityMainBinding
import iak.currencyquote.di.SingletonAppObject
import iak.currencyquote.helper.EMailHelper
import iak.currencyquote.helper.HumanisedTimeHelper
import iak.currencyquote.source.RepositoryConnectionStatus
import iak.currencyquote.ui.navigator.Navigator
import iak.currencyquote.ui.presenter.GlobalExchangeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity(
    private val repositoryConnectionStatus: RepositoryConnectionStatus = SingletonAppObject.repositoryConnectionStatus,
    private val connectStatusManager: ConnectStatusManager = SingletonAppObject.connectStatusManager
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
            binding.noInternet.isVisible = !it
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