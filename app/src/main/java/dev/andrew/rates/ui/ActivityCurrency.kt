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
import dev.andrew.rates.ui.presenter.GlobalExchangeViewModel
import java.util.*

class ActivityCurrency : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(): ActivityCurrency {
            return ActivityCurrency()
        }

        const val TAG = "ActivityCurrency"
    }

    private lateinit var binding: ActivityCurrencyBinding
    private val presenter: CurrencyViewModel by viewModels()

    private val globalEventsPresenter: GlobalExchangeViewModel by activityViewModels()

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
                bindSearchViewMenu(menu)
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

            hideFiat.setOnClickListener {
                listFiat.isVisible = !listFiat.isVisible
            }

            hideCrypto.setOnClickListener {
                listCrypto.isVisible = !listCrypto.isVisible
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
            if (it.isNotEmpty()) {
                visibleFiatBlock = true
                fiatCurrencyAdapter.dataList = CurrencyHelper.sortFiatByPopularity(it)
                    .map { RecCurrencyAdapter.AdapterData(it) }
            } else {
                visibleFiatBlock = false
            }
            bindCarriage()
        }

        presenter.getCryptoCurrencies().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                visibleCryptoBlock = true
                cryptoCurrencyAdapter.dataList = it
                    .map { RecCurrencyAdapter.AdapterData(it) }
            } else {
                visibleCryptoBlock = false
            }
            bindCarriage()
        }
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
            listFiat.isVisible = isVisible
        }

    var visibleCryptoBlock: Boolean
        get() = binding.cryptoLableContainer.isVisible
        set(isVisible) = with(binding) {
            cryptoLableContainer.isVisible = isVisible
            listCrypto.isVisible = isVisible
        }

    fun setCarriage(currency: ICurrency, carriage: GlobalExchangeViewModel.CurrencyCarriage) {
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

    private fun bindSearchViewMenu(menu: Menu) {
        searchView = menu.findItem(R.id.action_search)?.actionView as? SearchView ?: return

        with(searchView!!) {
            requestFocus()
            queryHint = Currency.getInstance(Locale.getDefault()).toString()
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