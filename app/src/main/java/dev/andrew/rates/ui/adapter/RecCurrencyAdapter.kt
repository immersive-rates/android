package dev.andrew.rates.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dev.andrew.rates.R
import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.databinding.CurrencyChipBinding
import dev.andrew.rates.helper.bindPrimaryCSelectionIndicator
import dev.andrew.rates.helper.bindSecondaryCSelectionIndicator
import dev.andrew.rates.helper.unbindSelectionIndicator
import dev.andrew.rates.ui.presenter.GlobalExchangeViewModel

typealias RecOnChipClick = (ICurrency, Boolean) -> Boolean

class RecCurrencyAdapter: RecyclerView.Adapter<RecCurrencyAdapter.CurrencyHolder>() {
    companion object {
        @JvmStatic
        private val DIFFER_CALLBACK = object : DiffUtil.ItemCallback<AdapterData>() {
            override fun areItemsTheSame(oldItem: AdapterData, newItem: AdapterData): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: AdapterData, newItem: AdapterData): Boolean {
                return oldItem.currency.name == newItem.currency.name
                        && oldItem.carriage == newItem.carriage
            }
        }

        const val TAG = "RecCurrencyAdapter"
    }

    data class AdapterData(
        val currency: ICurrency,
        var carriage: GlobalExchangeViewModel.CurrencyCarriage? = null
    )

    data class CurrencyHolder(
        private val binding: CurrencyChipBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: RecCurrencyAdapter.AdapterData) {
            binding.chip.id = R.id.chip
            binding.chip.tag = adapterData

            with(adapterData.currency) {
                binding.chip.text = name
            }

            when(adapterData.carriage) {
                GlobalExchangeViewModel.CurrencyCarriage.PRIMARY -> {
                    binding.chip.isChecked = true
                    binding.chip.bindPrimaryCSelectionIndicator()
                }
                GlobalExchangeViewModel.CurrencyCarriage.SECONDARY -> {
                    binding.chip.isChecked = true
                    binding.chip.bindSecondaryCSelectionIndicator()
                }
                null -> {
                    binding.chip.isChecked = false
                    binding.chip.unbindSelectionIndicator()
                }
            }
        }
    }

    inner class CurrencyByNameFilter: Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            return FilterResults().apply {
                val key = constraint.toString()
                    .trim()
                    .lowercase()
                values = dataList.filter { data ->
                    val name = data.currency.name.trim().lowercase()
                    name.contains(key)
                }
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            asyncDiffer.submitList(results.values as List<RecCurrencyAdapter.AdapterData>)
        }
    }

    fun filterCurrencyNameBy(query: String) {
        filter.filter(query)
    }

    fun setCarriage(currency: ICurrency, carriage: GlobalExchangeViewModel.CurrencyCarriage?): Boolean {
        val name = currency.name
        dataList.forEachIndexed { index, adapterData ->
            if (adapterData.currency.name == name) {
                adapterData.carriage = carriage
                notifyItemChanged(index)
                return true
            }
        }
        return false
    }

    private var filter = CurrencyByNameFilter()

    private val asyncDiffer = AsyncListDiffer(this, DIFFER_CALLBACK)

    private val sharedOnClickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.chip -> {
                (view.tag as? AdapterData)?.also { data ->
                    (view as? Chip)?.isChecked?.also { isChecked ->
                        recOnChipClick?.invoke(data.currency, isChecked)
                    }
                }
            }
        }
    }

    var recOnChipClick: RecOnChipClick? = null

    var dataList: List<AdapterData> = emptyList()
        set(value) {
            field = value
            asyncDiffer.submitList(field)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CurrencyChipBinding.inflate(inflater, parent, false)
        val holder = CurrencyHolder(binding)
        binding.root.setOnClickListener(sharedOnClickListener)
        return holder
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
    }

    override fun getItemCount() = asyncDiffer.currentList.size
}
