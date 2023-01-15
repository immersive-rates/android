package dev.andrew.rates

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

class OkHttpEventInspectorListener : EventListener() {
    companion object {
        const val TAG = "OkHttpEventListener"
    }
    override fun callStart(call: Call) {
        Log.d(TAG, "callStart")
        super.callStart(call)
    }

    override fun callFailed(call: Call, ioe: IOException) {
        Log.d(TAG, "callFailed")
        super.callFailed(call, ioe)
    }

    override fun callEnd(call: Call) {
        Log.d(TAG, "callEnd")
        super.callEnd(call)
    }

    override fun proxySelectStart(call: Call, url: HttpUrl) {
        Log.d(TAG, "proxySelectStart")
        super.proxySelectStart(call, url)
    }

    override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
        Log.d(TAG, "proxySelectEnd")
        super.proxySelectEnd(call, url, proxies)
    }

    override fun dnsStart(call: Call, domainName: String) {
        Log.d(TAG, "dnsStart")
        super.dnsStart(call, domainName)
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        Log.d(TAG, "dnsEnd")
        super.dnsEnd(call, domainName, inetAddressList)
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        Log.d(TAG, "connectStart")
        super.connectStart(call, inetSocketAddress, proxy)
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        Log.d(TAG, "connectFailed")
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        Log.d(TAG, "connectEnd")
        super.connectEnd(call, inetSocketAddress, proxy, protocol)
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        Log.d(TAG, "connectionAcquired")
        super.connectionAcquired(call, connection)
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        Log.d(TAG, "connectionReleased")
        super.connectionReleased(call, connection)
    }

    override fun secureConnectStart(call: Call) {
        Log.d(TAG, "secureConnectStart")
        super.secureConnectStart(call)
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        Log.d(TAG, "secureConnectEnd")
        super.secureConnectEnd(call, handshake)
    }

    override fun requestBodyStart(call: Call) {
        Log.d(TAG, "requestBodyStart")
        super.requestBodyStart(call)
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        Log.d(TAG, "requestBodyEnd")
        super.requestBodyEnd(call, byteCount)
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        Log.d(TAG, "requestFailed")
        super.requestFailed(call, ioe)
    }

    override fun requestHeadersStart(call: Call) {
        Log.d(TAG, "requestHeadersStart")
        super.requestHeadersStart(call)
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        Log.d(TAG, "requestHeadersEnd")
        super.requestHeadersEnd(call, request)
    }

    override fun responseBodyStart(call: Call) {
        Log.d(TAG, "responseBodyStart")
        super.responseBodyStart(call)
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        Log.d(TAG, "responseBodyEnd")
        super.responseBodyEnd(call, byteCount)
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        Log.d(TAG, "responseFailed")
        super.responseFailed(call, ioe)
    }

    override fun responseHeadersStart(call: Call) {
        Log.d(TAG, "responseHeadersStart")
        super.responseHeadersStart(call)
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        Log.d(TAG, "responseHeadersEnd")
        super.responseHeadersEnd(call, response)
    }

    override fun satisfactionFailure(call: Call, response: Response) {
        Log.d(TAG, "satisfactionFailure")
        super.satisfactionFailure(call, response)
    }

    override fun canceled(call: Call) {
        Log.d(TAG, "canceled")
        super.canceled(call)
    }

    override fun cacheHit(call: Call, response: Response) {
        Log.d(TAG, "cacheHit")
        super.cacheHit(call, response)
    }

    override fun cacheConditionalHit(call: Call, cachedResponse: Response) {
        Log.d(TAG, "cacheConditionalHit")
        super.cacheConditionalHit(call, cachedResponse)
    }

    override fun cacheMiss(call: Call) {
        Log.d(TAG, "cacheMiss")
        super.cacheMiss(call)
    }
}