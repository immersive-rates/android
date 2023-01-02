package iak.currencyquote.ui

import android.text.BoringLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import iak.currencyquote.data.ICurrency
import iak.currencyquote.databinding.CurrencyChipBinding
import iak.currencyquote.ui.presenter.GlobalExchangeViewModel

typealias OnCurrencyItemSelected = (ICurrency, Boolean) -> Boolean

class ActivityCurrencyAdapter : RecyclerView.Adapter<ActivityCurrencyAdapter.CurrencyHolder>() {
    companion object {
        const val TAG = "ActivityCurrencyAdapter"
    }

    inner class CurrencyHolder(
        private val binding: CurrencyChipBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: ICurrency) {
            with(binding) {
                chip.setOnCheckedChangeListener(null)
                chip.text = currency.name

                chip.setOnCheckedChangeListener { _, itChecked ->
                    if (!itemCheckListener(currency, itChecked)) {
                        chip.isChecked = !itChecked
                    }
                }
            }
        }

        fun check(carriage: GlobalExchangeViewModel.CurrencyCarriage) {
            Log.d(TAG, "Mark check to ${carriage.name}")
            binding.chip.isChecked = true
            when(carriage) {
                GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    binding.chip.bindPrimaryCSelectionIndicator()
                }
                GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                    binding.chip.bindSecondaryCSelectionIndicator()
                }
            }
        }

        fun uncheck() {
            binding.chip.isChecked = false
            binding.chip.unbindSelectionIndicator()
        }
    }

    private var filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            return FilterResults().apply {
                val keys = constraint.toString().trim().lowercase().split(' ')
                values = currencies.filter { currency ->
                    val name = currency.name.trim().lowercase()
                    keys.any { key ->
                        name.contains(key)
                    }
                }
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            differ.submitList(results.values as List<ICurrency>)
        }
    }

    private val LIST_DIFFER = object : DiffUtil.ItemCallback<ICurrency>() {
        override fun areItemsTheSame(oldItem: ICurrency, newItem: ICurrency) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ICurrency, newItem: ICurrency): Boolean {
            return newItem.name == oldItem.name && (!isInChecked(oldItem.name) && !checkedToReset.contains(oldItem.name))
        }

        override fun getChangePayload(oldItem: ICurrency, newItem: ICurrency): Any? {
            return if (checkedToReset.contains(oldItem.name)) {
                checkedToReset.remove(oldItem.name)
                false
            } else if (isInChecked(newItem.name)) {
                getCheckedByName(newItem.name)
            } else null
        }
    }

    private val differ = AsyncListDiffer(this, LIST_DIFFER)

    private var currencies: List<ICurrency> = emptyList()

    private var actualChecked = ArrayList<Pair<String, GlobalExchangeViewModel.CurrencyCarriage>>(2)
    private var checkedToReset = ArrayList<String>(2)

    var itemCheckListener: OnCurrencyItemSelected = {_, _ -> true}

    fun setCurrencies(data: List<ICurrency>) {
        currencies = data
        differ.submitList(data)
    }

    fun filter(query: String) {
        if (query.isEmpty()) {
            setCurrencies(currencies)
        } else {
            filter.filter(query)
        }
    }

    private fun isInChecked(name: String): Boolean {
        return actualChecked.any { it.first == name }
    }

    private fun getCheckedByName(name: String): Pair<String, GlobalExchangeViewModel.CurrencyCarriage> {
        return actualChecked.first() { it.first == name }
    }

    fun check(currency: ICurrency, carriage: GlobalExchangeViewModel.CurrencyCarriage): Boolean {
        return if (!isInChecked(currency.name) && actualChecked.add(
                currency.name to carriage
            )) {
            differ.submitList(differ.currentList)
            true
        } else false
    }

    fun uncheck(currency: ICurrency): Boolean {
        return if (actualChecked.removeIf { it.first == currency.name } && checkedToReset.add(currency.name)) {
            differ.submitList(differ.currentList)
            true
        } else false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        return CurrencyHolder(
            CurrencyChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        val currency = differ.currentList[position]
        if (isInChecked(currency.name)) {
            holder.check(getCheckedByName(currency.name).second)
        } else {
            holder.uncheck()
        }
        holder.bind(currency)
    }

    override fun onBindViewHolder(
        holder: CurrencyHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            val data = payloads[0]
            if (data is Boolean) {
                holder.uncheck()
            } else if (data is Pair<*, *>
                && data.second is GlobalExchangeViewModel.CurrencyCarriage) {
                holder.check(data.second as GlobalExchangeViewModel.CurrencyCarriage)
            }
        }
    }

    override fun getItemCount() =
        differ.currentList.size
}
