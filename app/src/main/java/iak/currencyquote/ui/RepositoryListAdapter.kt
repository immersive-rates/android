package iak.currencyquote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import iak.currencyquote.R
import iak.currencyquote.databinding.*
import iak.currencyquote.helper.HumanisedTimeHelper
import iak.currencyquote.source.currency.RepositoryInfo

typealias OnItemClick = (RepositoryInfo) -> Unit
typealias OnItemCheckChanged = (RepositoryInfo, Boolean) -> Unit

class RepositoryListAdapter : RecyclerView.Adapter<RepositoryListAdapter.RepositoryHolder>()
    , View.OnClickListener {
    inner class RepositoryHolder(
        val binding: RepositoryItemBinding
    ): RecyclerView.ViewHolder(binding.root)

    var onItemChecked: OnItemCheckChanged = {_,_->}

    inner class AdapterItemData(
        var isChecked: Boolean
    )

    fun setItems(repository: List<RepositoryInfo>) {
        repositoryList = repository
        for (i in (repository.indices)) {
            metaList.add(AdapterItemData(isChecked = false))
        }
        notifyDataSetChanged()
    }

    private var repositoryList: List<RepositoryInfo> = listOf()
    private var metaList: MutableList<AdapterItemData> = mutableListOf()

    private fun setMeta(repository: RepositoryInfo, adapterItemData: AdapterItemData): Boolean {
        val i = repositoryList.indexOf(repository)
        if (i > -1) {
            metaList[i] = adapterItemData
            return true
        }
        return false
    }

    private fun getMeta(repository: RepositoryInfo): AdapterItemData? {
        val i = repositoryList.indexOf(repository)
        return if (i > -1) {
            metaList[i]
        } else null
    }

    private fun updateMeta(repository: RepositoryInfo, adapterItemData: AdapterItemData) {
        if (setMeta(repository, adapterItemData))
            notifyDataSetChanged()
    }

    fun setChecked(repository: RepositoryInfo, isChecked: Boolean) {
        updateMeta(repository,
            getMeta(repository)?.also { it.isChecked = isChecked } ?: return)
    }

    fun getSelected(): RepositoryInfo? {
        val i = metaList.indexOfFirst { it.isChecked }
        return if (i > -1)
            repositoryList[i]
        else null
    }

    fun unselectAll() {
        for (i in metaList.indices) {
            metaList[i].also {
                if (it.isChecked) {
                    it.isChecked = false
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onClick(view: View) {
        val repositoryInfo = view.tag as? RepositoryInfo ?: return
        when (view.id) {
            R.id.ping_text -> {
                val checkable = view as? Checkable ?: return
                onItemChecked.invoke(repositoryInfo, checkable.isChecked)
            }
            else -> {
                getMeta(repositoryInfo)?.let { meta ->
                    onItemChecked.invoke(repositoryInfo, !meta.isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
        return RepositoryHolder(RepositoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        val repository = repositoryList[position]
        val adapterItemData = metaList[position]

        with(holder.binding) {
            root.tag = repository
            root.setOnClickListener(this@RepositoryListAdapter)

            pingText.tag = repository
            pingText.id = R.id.ping_text
            pingText.setOnClickListener(this@RepositoryListAdapter)

            with(repository) {
                hostedOn.text = root.resources.getString(
                    iak.currencyquote.R.string.hosted_on
                    , homePageUrl.toUri().host)
                coinIcon.removeAllViews()
                coinIcon.addView(
                    if (!isSupportFiat) {
                        CoinCryptoBinding.inflate(
                            LayoutInflater.from(root.context), root, false).root
                    } else if (!isSupportCrypto) {
                        CoinFiatBinding.inflate(
                            LayoutInflater.from(root.context), root, false).root
                    } else {
                        CoinCrypto8FiatBinding.inflate(
                            LayoutInflater.from(root.context), root, false).root
                    }
                )
                labelText.setText(labelRes)
                updatedIntervalText.text = root.resources.getString(R.string.updated_every,
                    HumanisedTimeHelper.humanisedInterval(updateInterval))
                pingText.apply {
                    isChecked = adapterItemData.isChecked
                }
            }
        }

    }

    override fun getItemCount() = repositoryList.size
}