package dev.andrew.rates.source

import android.util.Log
import androidx.lifecycle.MutableLiveData

class RepositoryConnectionStatus {
    companion object {
        const val TAG = "RepositoryConnectionStatus"
    }

    enum class Status {
        IDLE,
        CONNECTION_INPROGRESS,
        CONNECTION_FAIL,
        UPDATING_INPROGRESS,
        UPDATING_DONE,
        UPDATING_FAIL
    }

    private val statusMutableLive = MutableLiveData<Status>(Status.IDLE)
    val statusLive = statusMutableLive

    fun postStatus(status: Status) {
        Log.d(TAG, "postStatus $status")
        statusMutableLive.postValue(status)
    }
}