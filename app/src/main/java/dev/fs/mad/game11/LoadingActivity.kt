package dev.fs.mad.game11

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


@SuppressLint("CustomSplashScreen")
class LoadingActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000 // 2 seconds

    companion object {
        var gameURL = ""
        var endpoint = ""
        var policyURL = ""
        var appStatus = ""
        var apiResponse = ""
    }

    private lateinit var pref: SharedPreferences

    private val databaseUrl = "https://madproject-374e2-default-rtdb.firebaseio.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(1024, 1024)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_loading)

        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        getPolicy()
    }

    private fun getPolicy() {
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "$databaseUrl/MADDB/W11.json", null,
            { response ->
                try {
                    if (response != null) {
                        Log.d("firebase response", response.toString())
                        val pn = response.getString("packageName")
                        val gameStatus = response.getInt("gameStatus")
                        val url = response.getString("link")

                        Log.d("TAG", "$pn / $url")

                        if (packageName == pn) {
                            policyURL = url
                            if (gameStatus == 1) {
                                Handler().postDelayed({
                                    val intent = Intent(this@LoadingActivity, PolicyActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }, SPLASH_TIME_OUT.toLong())
                            } else {
                                endpoint = url
                                gameMaintenance()
                            }
                        }
                    } else {
                        // Handle the case where the response is null
                        Log.e("TAG", "Response is null")
                    }
                } catch (e: JSONException) {
                    Log.d(ContentValues.TAG, e.toString())
                }
            }
        ) { error ->
            Log.w(ContentValues.TAG, "Failed to read value.", error)
            // Handle different types of errors
            if (error.networkResponse != null) {
                Log.e("TAG", "Error Response Code: ${error.networkResponse.statusCode}")
            }
            if (error.message != null) {
                Log.e("TAG", "Error Message: ${error.message}")
            }
        }

        queue.add(jsonObjectRequest)
    }


    private fun gameMaintenance(){

        val connectAPI: RequestQueue = Volley.newRequestQueue(this)
        val requestBody = JSONObject()
        try {
            requestBody.put("appid", "W11")
            requestBody.put("package", "dev.fs.mad.game11")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val ep = "https://backend.madgamingdev.com/api/gameid?appid=W11&package=dev.fs.mad.game11"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, ep, requestBody,
            { response ->
                apiResponse = response.toString()

                Log.d("apiResponse: ", apiResponse)

                try {
                    val jsonData = JSONObject(apiResponse)
                    val decryptedData = Crypt.decrypt(
                        jsonData.getString("data"),
                        "21913618CE86B5D53C7B84A75B3774CD"
                    )
                    val gameData = JSONObject(decryptedData)

                    appStatus = jsonData.getString("gameKey")
                    gameURL = gameData.getString("gameURL")

                    Log.d("GAME URL",gameURL)

                    pref.edit().putString("gameURL", gameURL).apply()

                    // Using a Handler to delay the transition to the next activity
                    Handler().postDelayed({
                        val intent = Intent(this@LoadingActivity, WVActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, SPLASH_TIME_OUT.toLong())

                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            },
            { error ->
                Log.d("API:RESPONSE", error.toString())
            })

        Log.d("JSON REQUEST",jsonRequest.toString())

        connectAPI.add(jsonRequest)
    }
}