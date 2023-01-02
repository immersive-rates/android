package iak.currencyquote.di

import android.content.Context
import iak.currencyquote.source.RepositoryConnectionStatus
import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy


object SingletonOkHttpObject {
    private lateinit var aApplicationContext: Context
    fun provideApplicationContext(context: Context) {
        aApplicationContext = context
    }

    val repositoryConnectionStatus = SingletonAppObject.repositoryConnectionStatus

    val currencyRepositoryHttpClient by lazy(LazyThreadSafetyMode.NONE) {
        OkHttpClient.Builder()
            .eventListener(object : EventListener() {
                override fun callStart(call: Call) {
                    repositoryConnectionStatus
                        .postStatus(RepositoryConnectionStatus.Status.CONNECTION_INPROGRESS)
                    super.callStart(call)
                }

                override fun callFailed(call: Call, ioe: IOException) {
                    repositoryConnectionStatus
                        .postStatus(RepositoryConnectionStatus.Status.CONNECTION_FAIL)
                    super.callFailed(call, ioe)
                }

                override fun connectStart(
                    call: Call,
                    inetSocketAddress: InetSocketAddress,
                    proxy: Proxy
                ) {
                    repositoryConnectionStatus
                        .postStatus(RepositoryConnectionStatus.Status.UPDATING_INPROGRESS)
                    super.connectStart(call, inetSocketAddress, proxy)
                }

                override fun responseHeadersEnd(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        repositoryConnectionStatus
                            .postStatus(RepositoryConnectionStatus.Status.UPDATING_DONE)
                    } else {
                        repositoryConnectionStatus
                            .postStatus(RepositoryConnectionStatus.Status.UPDATING_FAIL)
                    }
                    super.responseHeadersEnd(call, response)
                }
            })
            .build()
    }
}