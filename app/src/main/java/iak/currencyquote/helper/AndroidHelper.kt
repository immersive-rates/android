package iak.currencyquote.helper

import android.os.Handler
import android.os.Looper

object AndroidHelper {
    fun runOnMainThread(runnable: Runnable) {
        val mainLooper = Looper.getMainLooper()
        if (mainLooper.isCurrentThread) {
            runnable.run()
        } else {
            Handler(Looper.getMainLooper()).post(runnable)
        }

    }
}