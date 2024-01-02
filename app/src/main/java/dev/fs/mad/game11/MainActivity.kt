package dev.fs.mad.game11

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dev.fs.mad.game11.databinding.ActivityMainBinding
import java.util.Objects

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val comboNumber = 7
    private val coef = 72
    private val coefW = 142
    private val coefE = 212
    private var position1 = 5
    private var position2 = 5
    private var position3 = 5
    private val slot = intArrayOf(1, 2, 3, 4, 5, 6, 7)

    private lateinit var rv1: RecyclerView
    private lateinit var rv2: RecyclerView
    private lateinit var rv3: RecyclerView
    private lateinit var layoutManager1: LayoutManager
    private lateinit var layoutManager2: LayoutManager
    private lateinit var layoutManager3: LayoutManager

    private lateinit var energyBallPrice: TextView
    private lateinit var myPower: TextView
    private lateinit var bet: TextView

    private var myCoins_val = 0
    private var bet_val = 0
    private var jackpot_val = 0

    private var firstRun = false

    private lateinit var gameLogic: GameFunction

    private lateinit var pref: SharedPreferences
    private lateinit var mp: MediaPlayer
    private lateinit var mpbet: MediaPlayer
    private lateinit var open: MediaPlayer
    private lateinit var win: MediaPlayer
    private lateinit var bgsound: MediaPlayer

    private val PREFS_NAME = "FirstRun"

    private var playmusic = 0
    private var playsound = 0
    private lateinit var music_off: ImageView
    private lateinit var music_on: ImageView
    private lateinit var soundon: ImageView
    private lateinit var soundoff: ImageView

    private lateinit var toast : Toast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(1024,1024)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bgsound = MediaPlayer.create(this, R.raw.bgmusic)
        bgsound.isLooping = true
        mp = MediaPlayer.create(this, R.raw.spin)
        mpbet = MediaPlayer.create(this, R.raw.bet)
        open = MediaPlayer.create(this, R.raw.open)
        win = MediaPlayer.create(this, R.raw.win)

        pref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        firstRun = pref.getBoolean("firstRun", true)

        if (firstRun) {
            playmusic = 1
            playsound = 1
            val editor = pref.edit()
            editor.putBoolean("firstRun", false)
            editor.apply()
        } else {
            playmusic = pref.getInt("music", 1)
            playsound = pref.getInt("sound", 1)
            checkmusic()
        }

        Log.d("MUSIC", playmusic.toString())

        // Initializations
        gameLogic = GameFunction()
        val settingsButton: ImageView = findViewById(R.id.settings)
        val spinButton: ImageButton = findViewById(R.id.spinButton)
        val plusButton: ImageButton = findViewById(R.id.plusButton)
        val minusButton: ImageButton = findViewById(R.id.minusButton)
        energyBallPrice = findViewById(R.id.energyBall)
        myPower = findViewById(R.id.energy)
        bet = findViewById(R.id.bet)
        val adapter = SpinnerAdapter()

        // RecyclerView settings
        rv1 = findViewById(R.id.spinner1)
        rv2 = findViewById(R.id.spinner2)
        rv3 = findViewById(R.id.spinner3)
        rv1.setHasFixedSize(true)
        rv2.setHasFixedSize(true)
        rv3.setHasFixedSize(true)

        layoutManager1 = LayoutManager(this)
        layoutManager1.setScrollEnabled(false)
        rv1.layoutManager = layoutManager1
        layoutManager2 = LayoutManager(this)
        layoutManager2.setScrollEnabled(false)
        rv2.layoutManager = layoutManager2
        layoutManager3 = LayoutManager(this)
        layoutManager3.setScrollEnabled(false)
        rv3.layoutManager = layoutManager3

        rv1.adapter = adapter
        rv2.adapter = adapter
        rv3.adapter = adapter
        rv1.scrollToPosition(position1)
        rv2.scrollToPosition(position2)
        rv3.scrollToPosition(position3)

        setText()
        updateText()

        // RecyclerView listeners
        rv1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv1.scrollToPosition(gameLogic.getPosition(0))
                    layoutManager1.setScrollEnabled(false)
                }
            }
        })

        rv2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv2.scrollToPosition(gameLogic.getPosition(1))
                    layoutManager2.setScrollEnabled(false)
                }
            }
        })
        rv3.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv3.scrollToPosition(gameLogic.getPosition(2))
                    layoutManager3.setScrollEnabled(false)
                    updateText()
                    if (gameLogic.getHasWon()) {
                        if (playsound == 1) {
                            win.start()
                        }
                        val inflater = LayoutInflater.from(this@MainActivity)
                        val layout = inflater.inflate(R.layout.win_splash, findViewById(R.id.win_splash))
                        val winCoins = layout.findViewById<TextView>(R.id.win_coins)
                        winCoins.text = gameLogic.getPrize()
                        toast = Toast(this@MainActivity)
                        toast.duration = Toast.LENGTH_SHORT
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.view = layout
                        toast.show()
                        gameLogic.setHasWon(false)
                    }
                }
            }
        })

        val credAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)

        // Button listeners
        spinButton.setOnClickListener {
            spinButton.startAnimation(credAnim)
            if (mp.isPlaying) {
                mp.stop()
                mp.prepare()
            }
            if (playsound == 1) {
                mp.start()
            }
            layoutManager1.setScrollEnabled(true)
            layoutManager2.setScrollEnabled(true)
            layoutManager3.setScrollEnabled(true)
            gameLogic.getSpinResults()
            position1 = gameLogic.getPosition(0) + coef
            position2 = gameLogic.getPosition(1) + coefW
            position3 = gameLogic.getPosition(2) + coefE
            rv1.smoothScrollToPosition(position1)
            rv2.smoothScrollToPosition(position2)
            rv3.smoothScrollToPosition(position3)
        }



        plusButton.setOnClickListener {
            if (mpbet.isPlaying) {
                mpbet.stop()
                mpbet.prepare()
            }

            if (playsound == 1) {
                mpbet.start()
            }
            gameLogic.betUp()
            updateText()
        }

        minusButton.setOnClickListener {
            if (mpbet.isPlaying) {
                mpbet.stop()
                mpbet.prepare()
            }
            if (playsound == 1) {
                mpbet.start()
            }
            gameLogic.betDown()
            updateText()
        }

        settingsButton.setOnClickListener {
            if (playsound == 1) {
                open.start()
            }
            showSettingsDialog()
        }

        MobileAds.initialize(
            this
        ) { }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun setText() {
        if (firstRun) {
            gameLogic.setMyCoins(1000)
            gameLogic.setBet(5)
            gameLogic.setJackpot(100000)

            val editor = pref.edit()
            editor.putBoolean("firstRun", false)
            editor.apply()
        } else {
            val coins = pref.getString("coins", "")
            val bet = pref.getString("bet", "")
            val jackpot = pref.getString("jackpot", "")
            Log.d("COINS", coins.toString())
            myCoins_val = coins!!.toInt()
            bet_val = bet!!.toInt()
            jackpot_val = jackpot!!.toInt()
            gameLogic.setMyCoins(myCoins_val)
            gameLogic.setBet(bet_val)
            gameLogic.setJackpot(jackpot_val)
        }
    }

    private fun updateText() {
        energyBallPrice.text = gameLogic.getJackpot()
        myPower.text = gameLogic.getMyCoins()
        bet.text = gameLogic.getBet()

        val editor = pref.edit()
        editor.putString("coins", gameLogic.getMyCoins())
        editor.putString("bet", gameLogic.getBet())
        editor.putString("jackpot", gameLogic.getJackpot())
        editor.apply()
    }

    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pic: ImageView = itemView.findViewById(R.id.spinner_item)
    }

    private inner class SpinnerAdapter : RecyclerView.Adapter<ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(this@MainActivity)
            val view = layoutInflater.inflate(R.layout.spinner_item, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val i = if (position < 7) position else position % comboNumber
            when (slot[i]) {
                1 -> holder.pic.setImageResource(R.drawable.combination_1)
                2 -> holder.pic.setImageResource(R.drawable.combination_2)
                3 -> holder.pic.setImageResource(R.drawable.combination_3)
                4 -> holder.pic.setImageResource(R.drawable.combination_4)
                5 -> holder.pic.setImageResource(R.drawable.combination_5)
                6 -> holder.pic.setImageResource(R.drawable.combination_6)
                7 -> holder.pic.setImageResource(R.drawable.combination_7)
            }
        }

        override fun getItemCount(): Int {
            return Int.MAX_VALUE
        }
    }

    private fun showSettingsDialog() {
        val dialog: Dialog

        dialog = Dialog(this, R.style.WinDialog)
        Objects.requireNonNull(dialog.window)?.setContentView(R.layout.settings)

        dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)

        val close = dialog.findViewById<ImageView>(R.id.close)
        close.setOnClickListener { dialog.dismiss() } // Close the dialog when the close button is clicked

        music_on = dialog.findViewById(R.id.music_on)
        music_on.setOnClickListener {
            playmusic = 0
            checkmusic()
            music_on.visibility = View.INVISIBLE
            music_off.visibility = View.VISIBLE
            val editor = pref.edit()
            editor.putInt("music", playmusic)
            editor.apply()
        }

        music_off = dialog.findViewById(R.id.music_off)
        music_off.setOnClickListener {
            playmusic = 1
            if (!bgsound.isPlaying()) {
                bgsound.start()
            }
            dialog.show()
            music_on.visibility = View.VISIBLE
            music_off.visibility = View.INVISIBLE
            val editor = pref.edit()
            editor.putInt("music", playmusic)
            editor.apply()
        }

        soundon = dialog.findViewById(R.id.sounds_on)
        soundon.setOnClickListener {
            playsound = 0
            soundon.visibility = View.INVISIBLE
            soundoff.visibility = View.VISIBLE
            val editor = pref.edit()
            editor.putInt("sound", playsound)
            editor.apply()
        }

        soundoff = dialog.findViewById(R.id.sounds_off)
        soundoff.setOnClickListener {
            playsound = 1
            dialog.show()
            soundon.visibility = View.VISIBLE
            soundoff.visibility = View.INVISIBLE
            val editor = pref.edit()
            editor.putInt("sound", playsound)
            editor.apply()
        }

        checkmusicdraw()
        checksounddraw()

        dialog.show()

    }

    override fun onPause() {
        super.onPause()
        toast.cancel()
        bgsound.pause()
    }

    override fun onResume() {
        super.onResume()
        checkmusic()
    }

    private fun checkmusic() {
        if (playmusic == 1) {
            bgsound.start()
        } else {
            bgsound.pause()
        }
    }

    private fun checkmusicdraw() {
        if (playmusic == 1) {
            music_on.visibility = View.VISIBLE
            music_off.visibility = View.INVISIBLE
        } else {
            music_on.visibility = View.INVISIBLE
            music_off.visibility = View.VISIBLE
        }
    }

    private fun checksounddraw() {
        if (playsound == 1) {
            soundon.visibility = View.VISIBLE
            soundoff.visibility = View.INVISIBLE
        } else {
            soundon.visibility = View.INVISIBLE
            soundoff.visibility = View.VISIBLE
        }
    }

}
