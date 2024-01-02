package dev.fs.mad.game11

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


private lateinit var preferences: SharedPreferences

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val accepted = preferences.getBoolean("accepted", false)
        if (accepted) {
            moveToMainActivity()
        }

        var policy: WebView = findViewById(R.id.policy)
        var lin: LinearLayout = findViewById(R.id.layout)
        var accept: Button = findViewById(R.id.accept)
        var denied: Button = findViewById(R.id.reject)

        //policy.loadUrl("file:///android_asset/index.html")
        policy.loadUrl(LoadingActivity.policyURL)

        accept.setOnClickListener {
            val editor = preferences.edit()
            editor.putBoolean("accepted", true)
            editor.apply()
            moveToMainActivity()
        }

        denied.setOnClickListener {
            finishAffinity()
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this@PolicyActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish this activity so that pressing back won't bring it back
    }
}