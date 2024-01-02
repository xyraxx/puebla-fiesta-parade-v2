package dev.fs.mad.game11

import java.lang.Math.random

class GameFunction {

    private var myCoins: Int = 0
    private var lines: Int = 1
    private var bet: Int = 0
    private var jackpot: Int = 0
    private var prize: Int = 0
    private var hasWon: Boolean = false

    private val slots = IntArray(3)

    fun getRandomInt(): Int {
        return (random() * 7 + 1).toInt()
    }

    fun getPosition(i: Int): Int {
        return slots[i] + 5
    }

    fun getSpinResults() {
        prize = 0
        for (i in slots.indices) {
            slots[i] = getRandomInt()
        }

        if (slots[0] == 7 || slots[1] == 7 || slots[2] == 7) {
            hasWon = true
            var count = 0
            for (a in slots) {
                if (a == 7) count++
            }

            when (count) {
                1 -> prize = bet * 5
                2 -> prize = bet * 10
                3 -> {
                    prize = jackpot
                    jackpot = 0
                }
            }
            myCoins += prize
        } else if (slots[0] == slots[1] && slots[1] == slots[2]) {
            hasWon = true
            when (slots[0]) {
                1 -> {
                    prize = bet * 2
                    myCoins += prize
                }
                2 -> {
                    prize = bet * 3
                    myCoins += prize
                }
                3 -> {
                    prize = bet * 5
                    myCoins += prize
                }
                4 -> {
                    prize = bet * 7
                    myCoins += prize
                }
                5 -> {
                    prize = bet * 10
                    myCoins += prize
                }
                6 -> {
                    prize = bet * 15
                    myCoins += prize
                }
            }
        } else {
            myCoins -= bet
            jackpot += bet
        }
    }

    // Setters
    fun setMyCoins(myCoins: Int) {
        this.myCoins = myCoins
    }

    fun setBet(bet: Int) {
        this.bet = bet
    }

    fun setJackpot(jackpot: Int) {
        this.jackpot = jackpot
    }

    fun setLines(lines: Int) {
        this.lines = lines
    }

    fun betUp() {
        if (bet < 100) {
            bet += 5
           // myCoins -= 5
        }
    }

    fun betDown() {
        if (bet > 5) {
            bet -= 5
          //  myCoins += 5
        }
    }

    fun setHasWon(hasWon: Boolean) {
        this.hasWon = hasWon
    }

    fun setPrize(prize: Int) {
        this.prize = prize
    }

    // Getters
    fun getMyCoins(): String {
        return myCoins.toString()
    }

    fun getLines(): String {
        return lines.toString()
    }

    fun getBet(): String {
        return bet.toString()
    }

    fun getJackpot(): String {
        return jackpot.toString()
    }

    fun getHasWon(): Boolean {
        return hasWon
    }

    fun getPrize(): String {
        return prize.toString()
    }
}