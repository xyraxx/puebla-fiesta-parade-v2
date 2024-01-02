package dev.fs.mad.game11

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import org.json.JSONException
import org.json.JSONObject

class JScript(private val context: Context) {

    @JavascriptInterface
    fun postMessage(name: String, data: String) {
        val eventValue = HashMap<String, Any>()

        if ("openWindow" == name) {
            try {
                val extLink = JSONObject(data) // Assuming the data is a JSON string
                val newWindow = Intent(Intent.ACTION_VIEW)
                newWindow.data = Uri.parse(extLink.getString("url"))
                newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(newWindow)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            eventValue[name] = data
        }
    }
}
