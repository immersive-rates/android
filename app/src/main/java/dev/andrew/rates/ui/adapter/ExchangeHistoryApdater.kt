package dev.andrew.rates.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.andrew.rates.data.ExchangeOperation
import dev.andrew.rates.databinding.ItemExchangeHistoryBinding
import kotlin.collections.ArrayList

class ExchangeHistoryAdapter : RecyclerView.Adapter<ExchangeHistoryAdapter.ItemHolder>() {

    class ItemHolder(
        private val binding: ItemExchangeHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exchangeOperation: ExchangeOperation, grayBackground: Boolean) {
            with(binding) {
                marketName.setText(exchangeOperation.repositoryInfo.shortLabelRes)
                fromText.text = exchangeOperation.fromCount.toString()
                toText.text = exchangeOperation.toCount.toString()
                currencyFrom.text = exchangeOperation.fromName
                currencyTo.text = exchangeOperation.toName
            }
        }
    }

    private var items: MutableList<ExchangeOperation> = ArrayList(0)

    fun setItems(items: List<ExchangeOperation>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExchangeHistoryBinding.inflate(inflater, parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(items[position], position % 2 == 0)
    }

    override fun getItemCount() = items.size

    fun getItemAt(position: Int) = items[position]

    fun removeItemAt(position: Int) {
        items.removeAt(position)
        notifyDataSetChanged()
    }
}
