package dev.fs.mad.game11

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        Toast.makeText(context, "Received Notification: $title - $message", Toast.LENGTH_SHORT)
            .show()
    }
}