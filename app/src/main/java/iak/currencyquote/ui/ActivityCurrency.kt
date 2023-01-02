package iak.currencyquote.ui

import android.app.SearchManager
import android.content.Context
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
import iak.currencyquote.R
import iak.currencyquote.data.ICurrency
import iak.currencyquote.databinding.ActivityCurrencyBinding
import iak.currencyquote.helper.CurrencyHelper
import iak.currencyquote.ui.presenter.CurrencyViewModel
import iak.currencyquote.ui.presenter.GlobalExchangeViewModel
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

    private lateinit var fiatCurrencyAdapter: ActivityCurrencyAdapter
    private lateinit var cryptoCurrencyAdapter: ActivityCurrencyAdapter

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
                fiatCurrencyAdapter = ActivityCurrencyAdapter().also {
                    it.itemCheckListener = presenter::onCurrencyItemSelected
                }

                adapter = fiatCurrencyAdapter
                layoutManager = GridLayoutManager(
                    requireContext(),
                    4, RecyclerView.VERTICAL, false
                )
            }

            listCrypto.apply {
                cryptoCurrencyAdapter = ActivityCurrencyAdapter().also {
                    it.itemCheckListener = presenter::onCurrencyItemSelected
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

        presenter.getFirstCurrency().observe(viewLifecycleOwner) {
            presenter.onFirstCurrencyUpdated(it)
        }

        presenter.getSecondCurrency().observe(viewLifecycleOwner) {
            presenter.onSecondCurrencyUpdated(it)
        }
    }

    override fun onStart() {
        super.onStart()

        presenter.getFiatCurrencies().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                visibleFiatBlock = true
                fiatCurrencyAdapter.setCurrencies(CurrencyHelper.sortFiatByPopularity(it))
            } else {
                visibleFiatBlock = false
                fiatCurrencyAdapter.setCurrencies(it)
            }
        }

        presenter.getCryptoCurrencies().observe(viewLifecycleOwner) {
            visibleCryptoBlock = it.isNotEmpty()
            cryptoCurrencyAdapter.setCurrencies(it)
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

    fun check(currency: ICurrency, carriage: GlobalExchangeViewModel.CurrencyCarriage) {
        if (fiatCurrencyAdapter.check(currency, carriage)) {
            cryptoCurrencyAdapter.check(currency, carriage)
        }
    }

    fun uncheck(currency: ICurrency) {
        if (fiatCurrencyAdapter.uncheck(currency)) {
            cryptoCurrencyAdapter.uncheck(currency)
        }
    }

    fun updateSecondaryChip() {
    }

    fun updatePrimaryChip() {
    }

    private fun bindSearchViewMenu(menu: Menu) {
        val searchView: SearchView = menu.findItem(R.id.action_search)?.actionView as? SearchView ?: return

        with(searchView) {
            requestFocus()
            queryHint = Currency.getInstance(Locale.getDefault()).toString()
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    fiatCurrencyAdapter.filter(newText)
                    cryptoCurrencyAdapter.filter(newText)
                    return false
                }

            })
        }
    }
}