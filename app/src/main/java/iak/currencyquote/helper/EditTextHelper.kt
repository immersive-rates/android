package iak.currencyquote.helper

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.widget.addTextChangedListener

object EditTextHelper {
    fun addOnFinishedTypingListener(editText: EditText, runnable: Runnable) {
        val handler = Handler(Looper.getMainLooper())
        editText.addTextChangedListener(
            onTextChanged = { _, _, _, _ -> handler.removeCallbacks(runnable) },
            afterTextChanged = { handler.postDelayed(runnable, 600) }
        )
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || handler.hasCallbacks(runnable)) {
                    handler.removeCallbacks(runnable)
                    runnable.run()
                }
            }
            false
        }
    }
}