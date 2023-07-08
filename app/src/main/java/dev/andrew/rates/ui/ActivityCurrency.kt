package dev.andrew.rates.ui

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.andrew.rates.R
import dev.andrew.rates.databinding.ActivityCurrencyBinding
import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.helper.CurrencyHelper
import dev.andrew.rates.ui.adapter.RecCurrencyAdapter
import dev.andrew.rates.ui.presenter.CurrencyViewModel
import dev.andrew.rates.ui.presenter.SharedExchangeViewModel
import java.util.*

class ActivityCurrency : Fragment() {
    companion object {
        private const val ARGH_QUERY_HINT_STRI = "ARGH_QUERY_HINT_STRI"
        @JvmStatic
        fun newInstance(queryHint: String): ActivityCurrency {
            return ActivityCurrency().also { activity ->
                activity.arguments = Bundle().apply {
                    putString(ARGH_QUERY_HINT_STRI, queryHint)
                }
            }
        }

        const val TAG = "ActivityCurrency"
    }

    private lateinit var binding: ActivityCurrencyBinding
    private val presenter: CurrencyViewModel by viewModels()

    private val globalEventsPresenter: SharedExchangeViewModel by activityViewModels()

    private val sharedRecyclerPool = RecyclerView.RecycledViewPool()
    private lateinit var fiatCurrencyAdapter: RecCurrencyAdapter
    private lateinit var cryptoCurrencyAdapter: RecCurrencyAdapter

    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityCurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bindActivity(this)
        presenter.bindCurrencyCarriage(globalEventsPresenter.currencyCarriage)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.currency_menu, menu)
                bindSearchViewMenu(menu, arguments!!.getString(ARGH_QUERY_HINT_STRI)!!)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        with(binding) {
            listFiat.apply {
                setRecycledViewPool(sharedRecyclerPool)

                fiatCurrencyAdapter = RecCurrencyAdapter().also {
                    it.recOnChipClick = presenter::onCurrencyItemSelected
                }

                adapter = fiatCurrencyAdapter
                layoutManager = GridLayoutManager(
                    requireContext(),
                    4, RecyclerView.VERTICAL, false
                )
            }

            listCrypto.apply {
                setRecycledViewPool(sharedRecyclerPool)

                cryptoCurrencyAdapter = RecCurrencyAdapter().also {
                    it.recOnChipClick = presenter::onCurrencyItemSelected
                }

                adapter = cryptoCurrencyAdapter
                layoutManager = GridLayoutManager(
                    requireContext(),
                    4, RecyclerView.VERTICAL, false
                )
            }
        }

        globalEventsPresenter.currencyCarriage.observe(viewLifecycleOwner) {
            presenter.onCarriageChanged(it)
        }

        bindCarriage()
    }

    override fun onStart() {
        super.onStart()

        presenter.getFiatCurrencies().observe(viewLifecycleOwner) {
            if (it == null) {
                visibleFiatBlock = false
                enableFiatListProgress(false)
            } else if (it.isNotEmpty()) {
                visibleFiatBlock = true
                enableFiatListProgress(false)
                fiatCurrencyAdapter.dataList = CurrencyHelper.sortFiatByPopularity(it)
                    .map { RecCurrencyAdapter.AdapterData(it) }
            } else if(it.isEmpty()) {
                visibleFiatBlock = true
                enableFiatListProgress(true)
                fiatCurrencyAdapter.dataList = emptyList()
            }
            bindCarriage()
        }

        presenter.getCryptoCurrencies().observe(viewLifecycleOwner) {
            if (it == null) {
                visibleCryptoBlock = false
                enableCryptoListProgress(false)
            } else if (it.isNotEmpty()) {
                visibleCryptoBlock = true
                enableCryptoListProgress(false)
                cryptoCurrencyAdapter.dataList = it
                    .map { RecCurrencyAdapter.AdapterData(it) }
            } else if(it.isEmpty()) {
                visibleCryptoBlock = true
                enableCryptoListProgress(true)
                cryptoCurrencyAdapter.dataList = emptyList()
            }
            bindCarriage()
        }
    }

    fun enableFiatListProgress(isVisible: Boolean) {
        binding.listFiatProgress.isVisible = isVisible
    }

    fun enableCryptoListProgress(isVisible: Boolean) {
        binding.listCryptoProgress.isVisible = isVisible
    }

    private fun bindCarriage() {
        presenter.getFirstCurrency().observe(viewLifecycleOwner) {
            presenter.onFirstCurrencyUpdated(it)
        }

        presenter.getSecondCurrency().observe(viewLifecycleOwner) {
            presenter.onSecondCurrencyUpdated(it)
        }
    }

    var visibleFiatBlock: Boolean
        get() = binding.fiatLableContainer.isVisible
        set(isVisible) = with(binding) {
            fiatLableContainer.isVisible = isVisible
            fiatContainer.isVisible = isVisible
        }

    var visibleCryptoBlock: Boolean
        get() = binding.cryptoLableContainer.isVisible
        set(isVisible) = with(binding) {
            cryptoLableContainer.isVisible = isVisible
            cryptoContainer.isVisible = isVisible
        }

    fun setCarriage(currency: ICurrency, carriage: SharedExchangeViewModel.CurrencyCarriage) {
        if (!fiatCurrencyAdapter.setCarriage(currency, carriage)) {
            cryptoCurrencyAdapter.setCarriage(currency, carriage)
        }
    }

    fun clearCarriage(currency: ICurrency) {
        if (!fiatCurrencyAdapter.setCarriage(currency, null)) {
            cryptoCurrencyAdapter.setCarriage(currency, null)
        }
    }

    fun updateSecondaryChip() {
    }

    fun updatePrimaryChip() {
    }

    fun clearSearchQuery() {
        searchView?.setQuery("", true)
    }

    private fun bindSearchViewMenu(menu: Menu, aQueryHint: String) {
        searchView = menu.findItem(R.id.action_search)?.actionView as? SearchView ?: return

        with(searchView!!) {
            requestFocus()
            queryHint = aQueryHint
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    fiatCurrencyAdapter.filterCurrencyNameBy(query)
                    cryptoCurrencyAdapter.filterCurrencyNameBy(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    fiatCurrencyAdapter.filterCurrencyNameBy(newText)
                    cryptoCurrencyAdapter.filterCurrencyNameBy(newText)
                    return false
                }

            })
        }
    }
}