package dev.andrew.rates.helper

import android.content.Context
import android.content.Intent
import android.net.Uri

object EMailHelper {
    fun navToEmailCompose(context: Context, email: String) {
        val intent = Intent(Intent.ACTION_SENDTO,
            Uri.parse("mailto:${email}"))
        context.startActivity(Intent.createChooser(intent, "Email"))
    }
}