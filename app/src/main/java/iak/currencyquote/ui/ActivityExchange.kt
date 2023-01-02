package iak.currencyquote.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.SearchView
import androidx.core.animation.doOnEnd
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iak.currencyquote.R
import iak.currencyquote.databinding.ActivityExchangerBinding
import iak.currencyquote.helper.CurrencyHelper
import iak.currencyquote.helper.EditTextHelper
import iak.currencyquote.ui.presenter.ExchangeViewModel
import iak.currencyquote.ui.presenter.GlobalExchangeViewModel


class ActivityExchange : Fragment() {
    companion object {
        const val TAG = "ActivityExchange"
    }

    interface ExchangerHandler {
        fun onPrimarySelected()
        fun onSecondSelected()
    }

    private val presenter: ExchangeViewModel by viewModels()

    private val globalEventsPresenter: GlobalExchangeViewModel by activityViewModels()

    private lateinit var binding: ActivityExchangerBinding

    private lateinit var exchangeHistoryAdapter: ExchangeHistoryAdapter

    private var changesIsBlocked = false

    private var inTextSkipEvent = false
    private var outTextSkipEvent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityExchangerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bindActivity(this)

        presenter.bindCurrencyCarriage(globalEventsPresenter.currencyCarriage)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.refresh_data -> {
                        emmitExchangeOperation()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        with(binding.exchange) {
            toggleCurrencies.setOnClickListener {

                toggleCurrencies.isEnabled = false
                playToggleAnimation(
                    toggleCurrenciesOut.chip.text.toString(),
                    toggleCurrenciesIn.chip.text.toString()
                ) {
                    emmitExchangeOperation()

                    toggleCurrencies.isEnabled = true
                }

                presenter.onCurrencyToggleTap()
            }

            toggleCurrenciesIn.chip.apply {
                bindPrimaryCIndicator()
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        presenter.onPrimarySelected()
                    }
                }
                setOnClickListener {
                    (activity as? ExchangerHandler)?.onPrimarySelected()
                }
            }

            toggleCurrenciesOut.chip.apply {
                bindSecondaryCIndicator()
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        presenter.onSecondSelected()
                    }
                }
                setOnClickListener {
                    (activity as? ExchangerHandler)?.onSecondSelected()
                }
            }

            editInputCurrency.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    presenter.onInputFocusChange(hasFocus)
                }
                EditTextHelper.addOnFinishedTypingListener(this) {
                    if (!changesIsBlocked) {
                        if (!inTextSkipEvent)
                            presenter.onCurrencyInCountInput(text.toString())
                        else inTextSkipEvent = false
                    }
                }
            }

            editOutputCurrency.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    presenter.onOutputFocusChange(hasFocus)
                }
                EditTextHelper.addOnFinishedTypingListener(this) {
                    if (!changesIsBlocked) {
                        if (!outTextSkipEvent)
                            presenter.onCurrencyOutputCountInput(text.toString())
                        else outTextSkipEvent = false
                    }
                }
            }
        }

        exchangeHistoryAdapter = ExchangeHistoryAdapter().also {
            presenter.bindAdapter(it)
        }

        with(binding.exchangeHistory) {
            list.apply {
                adapter = exchangeHistoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback
                    (0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        presenter.onHistoryItemSwiped(viewHolder)
                    }
                }).attachToRecyclerView(this)
            }
        }

        fun focusPrimaryCurrency(isFocused: Boolean) {
            with(binding.exchange) {
                toggleCurrenciesIn.chip.isChecked = isFocused
                if (isFocused) {
                    editInputCurrency.requestFocus()
                } else {
                    editInputCurrency.clearFocus()
                }
            }
        }

        fun focusSecondaryCurrency(isFocused: Boolean) {
            with(binding.exchange) {
                toggleCurrenciesOut.chip.isChecked = isFocused
                if (isFocused) {
                    editOutputCurrency.requestFocus()
                } else {
                    editOutputCurrency.clearFocus()
                }
            }
        }

        globalEventsPresenter.currencyCarriage.observe(viewLifecycleOwner) { carriage ->
            when (carriage) {
                GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    focusSecondaryCurrency(false)
                    focusPrimaryCurrency(true)
                }
                GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                    focusPrimaryCurrency(false)
                    focusSecondaryCurrency(true)
                }
                else -> {
                    focusSecondaryCurrency(false)
                    focusPrimaryCurrency(true)
                }
            }
        }

        presenter.getExchangeHistory().observe(viewLifecycleOwner) {
            exchangeHistoryAdapter.setItems(it)
        }

        presenter.getFirstCurrency().observe(viewLifecycleOwner) {
            binding.exchange.editInputCurrency.hint =
                resources.getString(
                    R.string.currency_value_input_hint,
                    CurrencyHelper.getCurrencySymbol(it.name)
                )
            if (!changesIsBlocked) {
                setInChipText(it.name)
            }
        }

        presenter.getSecondCurrency().observe(viewLifecycleOwner) {
            binding.exchange.editOutputCurrency.hint =
                resources.getString(
                    R.string.currency_value_input_hint,
                    CurrencyHelper.getCurrencySymbol(it.name)
                )
            if (!changesIsBlocked) {
                setOutChipText(it.name)
            }
        }
    }

    private fun setInChipText(text: String) {
        binding.exchange.toggleCurrenciesIn.chip.text = text
    }

    private fun setOutChipText(text: String) {
        binding.exchange.toggleCurrenciesOut.chip.text = text
    }

    private fun emmitExchangeOperation() {
        with(binding.exchange) {
            if (editInputCurrency.isFocused) {
                editInputCurrency.text = editInputCurrency.text
            } else if (editOutputCurrency.isFocused) {
                editOutputCurrency.text = editOutputCurrency.text
            }
        }
    }

    fun setInCurrencyValue(text: String) {
        inTextSkipEvent = true
        binding.exchange.editInputCurrency.setText(text)
    }

    fun setOutCurrencyValue(text: String) {
        outTextSkipEvent = true
        binding.exchange.editOutputCurrency.setText(text)
        binding.exchange.layoutOutputCurrency.setEndIconActivated(true)
    }

    private fun playToggleAnimation(newInText: String, newOutText: String, onFinished: () -> Unit) {
        changesIsBlocked = true

        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(
                binding.exchange.toggleCurrencies,
                View.ROTATION,
                binding.exchange.toggleCurrencies.rotation + 190))

            play(ObjectAnimator.ofFloat(binding.exchange.toggleCurrenciesIn.chip, View.ALPHA, 0f).apply {
                doOnEnd {
                    changesIsBlocked = false
                    setInChipText(newInText)
                }
                interpolator = DecelerateInterpolator()
            }).before(ObjectAnimator.ofFloat(binding.exchange.toggleCurrenciesIn.chip, View.ALPHA, 1f))

            play(ObjectAnimator.ofFloat(binding.exchange.toggleCurrenciesOut.chip, View.ALPHA, 0f).apply {
                doOnEnd {
                    changesIsBlocked = false
                    setOutChipText(newOutText)
                    onFinished.invoke()
                }
                interpolator = DecelerateInterpolator()
            }).before(ObjectAnimator.ofFloat(binding.exchange.toggleCurrenciesOut.chip, View.ALPHA, 1f))

            duration = 600L
            interpolator = DecelerateInterpolator()

            start()
        }
    }
}