package dev.andrew.rates

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ConnectStatusManager(
    context: Context
) {
    private val mutableState = MutableLiveData<Boolean>()
    val isConnect: LiveData<Boolean> = mutableState

    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            mutableState.postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            mutableState.postValue(false)
        }
    }

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    it.addTransportType(NetworkCapabilities.TRANSPORT_LOWPAN)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.addTransportType(NetworkCapabilities.TRANSPORT_USB)
                }
            }.build()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, NetworkCallback())
    }
}