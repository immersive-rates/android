package dev.andrew.rates

import dev.andrew.rates.di.SingletonOkHttpObject
import dev.andrew.rates.source.RepositoryConnectionStatus
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

class CurrencyRepositoryClientListener(
    private val repositoryConnectionStatus: RepositoryConnectionStatus
): EventListener() {
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
}