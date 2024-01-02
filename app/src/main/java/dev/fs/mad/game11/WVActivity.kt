package dev.fs.mad.game11

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WVActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wv)

        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val webApp: WebSettings = findViewById(R.id.webApp)
        webApp.addJavascriptInterface(JScript(this), "jsBridge")
        webApp.loadUrl(pref.getString("gameURL", "").toString())
    }
}