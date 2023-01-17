package dev.andrew.rates.source

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dev.andrew.rates.BuildConfig
import dev.andrew.rates.data.Crypto
import dev.andrew.rates.data.Fiat
import dev.andrew.rates.data.ICurrency
import dev.andrew.rates.data.Rate
import dev.andrew.rates.source.currency.*
import dev.andrew.rates.source.exception.CurrencyPairNotSupported
import kotlinx.coroutines.*

class CurrencySourceManager(
    private val appSettings: AppSettings
) {
    companion object {
        const val TAG = "CurrencySourceManager"
        const val EXHANDLER_TAG = "ExHandler_CurrencySourceManager"
    }
    // todo, make universal handle UndefineHostException
    private val exHandler = CoroutineExceptionHandler { _, throwable ->
        catchRuntimeException(throwable)
        Log.wtf(EXHANDLER_TAG, throwable)
    }

    private fun catchRuntimeException(e: Throwable) {
        if (e is CurrencyPairNotSupported) {
            lastRuntimeExceptionLive.postValue(e)
        }
    }

    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + exHandler)

    private val mutableCurrentRepositoryLive: MutableLiveData<ICurrencySource> = MutableLiveData()
    private var currentRepository: ICurrencySource? = null
    val currentRepositoryLive: LiveData<ICurrencySource> = mutableCurrentRepositoryLive

    private val registeredSourceClassById = HashMap<Int, Class<out ICurrencySource>>()
    private val registeredSources = ArrayList<RepositoryInfo>()

    private val lastRuntimeExceptionLive = MutableLiveData<Exception>()
    val lastRuntimeException: LiveData<Exception> = lastRuntimeExceptionLive

    private fun getAvailableCurrencies(repo: ICurrencySource): List<ICurrency>? {
        try {
            return repo.getAvailableCurrencies()
        } catch (e: Exception) {
            catchRuntimeException(e)
        }
        return null
    }

    private fun getLatestRate(repo: ICurrencySource, from: ICurrency, to: ICurrency): Rate? {
        try {
            return repo.getLatestRate(from, to)
        } catch (e: Exception) {
            catchRuntimeException(e)
        }
        return null
    }


    init {
        registerSource(RussianCentralBankRepository.INFO, RussianCentralBankRepository::class.java)
        registerSource(BinanceRepository.INFO, BinanceRepository::class.java)
        registerSource(Fawazahmed0Repository.INFO, Fawazahmed0Repository::class.java)
        registerSource(MOEXRepository.INFO, MOEXRepository::class.java)

        if (BuildConfig.DEBUG) {
            registerSource(SimpleRepository.INFO, SimpleRepository::class.java)
        }

        restoreLastUsedSource()
    }

    private val currencyListLive: LiveData<List<ICurrency>?> = MediatorLiveData<List<ICurrency>?>().apply {
        addSource(currentRepositoryLive) { repo ->
            currentRepository = repo
            postValue(emptyList())
            ioCoroutineScope.launch {
                var currencyList: List<ICurrency>? = null
                while (currencyList == null) {
                    currencyList = getAvailableCurrencies(repo)
                    delay(1000)
                }
                postValue(currencyList)
            }
        }
    }

    /*
        Return: list or null
     */
    val fiatCurrencyLive: LiveData<List<Fiat>> = MediatorLiveData<List<Fiat>>().apply {
        addSource(currencyListLive) { currencyList ->
            postValue(if (getInfo().isSupportFiat) {
                currencyList?.filterIsInstance<Fiat>()
            } else {
                null
            })
        }
    }

    /*
        Return: list or null
     */
    val cryptoCurrencyLive: LiveData<List<Crypto>> = MediatorLiveData<List<Crypto>>().apply {
        addSource(currencyListLive) { currencyList ->
            postValue(if (getInfo().isSupportCrypto) {
                currencyList?.filterIsInstance<Crypto>()
            } else {
                null
            })
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
                val rate = getLatestRate(repo, from, to)
                if (rate == null) {
                    result.postValue(0F)
                } else {
                    result.postValue(toCount * rate.rate)
                }
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
//            if (repo is ICachedCurrencySource) {
//                return CachedCurrencyRepositoryWrapper(repo)
//            }
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