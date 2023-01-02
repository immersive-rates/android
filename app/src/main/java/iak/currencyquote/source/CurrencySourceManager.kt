package iak.currencyquote.source

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import iak.currencyquote.data.Crypto
import iak.currencyquote.data.Fiat
import iak.currencyquote.data.ICurrency
import iak.currencyquote.source.currency.*
import iak.currencyquote.source.currency.cache.CachedCurrencyRepositoryWrapper
import iak.currencyquote.source.currency.cache.ICachedCurrencySource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencySourceManager(
    private val appSettings: AppSettings
) {
    companion object {
        const val TAG = "CurrencySourceManager"
    }
    // todo, make universal handle UndefineHostException
    private val exHandler = CoroutineExceptionHandler { _, throwable ->
        Log.wtf(TAG, throwable)
    }

    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + exHandler)

    private val mutableCurrentRepositoryLive: MutableLiveData<ICurrencySource> = MutableLiveData()
    val currentRepositoryLive: LiveData<ICurrencySource> = mutableCurrentRepositoryLive

    private val registeredSourceClassById = HashMap<Int, Class<out ICurrencySource>>()
    private val registeredSources = ArrayList<RepositoryInfo>()

    init {
        registerSource(CbRFRepository.INFO, CbRFRepository::class.java)
        registerSource(BinanceRepository.INFO, BinanceRepository::class.java)
        registerSource(Fawazahmed0Repository.INFO, Fawazahmed0Repository::class.java)
        registerSource(MOEXRepository.INFO, MOEXRepository::class.java)

        restoreLastUsedSource()
    }

    val fiatCurrencyLive: LiveData<List<Fiat>> = MediatorLiveData<List<Fiat>>().apply {
        addSource(currentRepositoryLive) { repo ->
            ioCoroutineScope.launch {
                postValue(if (repo.getInfo().isSupportFiat) {
                    repo.getAvailableCurrencies().filterIsInstance<Fiat>()
                } else {
                    emptyList()
                })
            }
        }
    }

    val cryptoCurrencyLive: LiveData<List<Crypto>> = MediatorLiveData<List<Crypto>>().apply {
        addSource(currentRepositoryLive) { repo ->
            ioCoroutineScope.launch {
                postValue(if (repo.getInfo().isSupportCrypto) {
                    repo.getAvailableCurrencies().filterIsInstance<Crypto>()
                } else {
                    emptyList()
                })
            }
        }
    }

    fun getInfoList(): List<RepositoryInfo> {
        return registeredSources
    }

    fun getInfo(sourceId: Int): RepositoryInfo {
        return getInfoList().first { it.id == sourceId }
    }

    fun getInfo(): RepositoryInfo {
        return getInfo(appSettings.lastUsedSourceId)
    }

    fun exchange(from: ICurrency, to: ICurrency, toCount: Float): LiveData<Float> {
        val result = MutableLiveData<Float>()
        if (toCount >= 0) {
            ioCoroutineScope.launch {
                val repo = currentRepositoryLive.value!!
                val rate = repo.getLatestRate(from, to)
                result.postValue(toCount * rate.rate)
            }
        } else {
            result.postValue(0F)
        }
        return result
    }

    @MainThread
    fun setCurrentRepository(repositoryInfo: RepositoryInfo): Boolean {
        if (repositoryInfo.id != currentRepositoryLive.value?.getInfo()?.id) {
            val repository = instanceRepositoryById(repositoryInfo.id) ?: return false
            appSettings.lastUsedSourceId = repositoryInfo.id
            mutableCurrentRepositoryLive.value = repository
            return true
        } else {
            return false
        }
    }

    private fun instanceRepositoryById(repositoryId: Int): ICurrencySource? {
        val repoClass = registeredSourceClassById.getOrDefault(repositoryId, null)
        if (repoClass != null) {
            val repo = repoClass.getConstructor().newInstance()
            if (repo is ICachedCurrencySource) {
                return CachedCurrencyRepositoryWrapper(repo)
            }
            return repo
        } else {
            throw RuntimeException("UndefinedRepository")
        }
    }

    private fun registerSource(info: RepositoryInfo, source: Class<out ICurrencySource>) {
        registeredSources.add(info)
        registeredSourceClassById[info.id] = source
    }

    private fun restoreLastUsedSource() {
        ioCoroutineScope.launch {
            mutableCurrentRepositoryLive.postValue(
                instanceRepositoryById(appSettings.lastUsedSourceId))
        }
    }
}