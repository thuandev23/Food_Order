package com.example.food_ordering.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.food_ordering.R

class SpinMiniGameActivity : AppCompatActivity(), Animation.AnimationListener {
    private var count = 0
    private var flag = false
    private var powerButton: ImageView? = null
    private var hasWatchedAd = false
    private var spinCount = 0
    private val prizes = intArrayOf(20000, 15000, 60000, 50000, 10000, 12000, 30000, 18000, 40000, 30000, 16000, 25000)
    private var mSpinDuration: Long = 0
    private var mSpinRevolution = 0f
    private var pointerImageView: ImageView? = null
    private var infoText: TextView? = null
    private var prizeText = "N/A"

    // Final point of rotation defined right here
    private val end = Math.floor(Math.random() * 3600).toInt() // random : 0-360
    private val numOfPrizes = prizes.size // quantity of prize
    private val degreesPerPrize = 360 / numOfPrizes // size of sector per prize in degrees
    private val shift = 0 // shift where the arrow points
    private val prizeIndex = (shift + end) % numOfPrizes

    private lateinit var spinStartSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_mini_game)

        // Initialize sounds
        spinStartSound = MediaPlayer.create(this, R.raw.audiospin)

        powerButton = findViewById(R.id.powerButton)
        powerButton!!.setOnTouchListener(PowerTouchListener())
        intSpinner()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

    }

    private fun intSpinner() {
        pointerImageView = findViewById(R.id.imageWheel)
        infoText = findViewById(R.id.infoText)
    }
    private fun startSpinner() {
        mSpinRevolution = 3600f
        mSpinDuration = 10000

        if (count >= 30) {
            mSpinDuration = 1000
            mSpinRevolution = (3600 * 2).toFloat()
        }
        if (count >= 60) {
            mSpinDuration = 15000
            mSpinRevolution = (3600 * 3).toFloat()
        }
        prizeText = "Prize is : ${prizes[prizeIndex]}"
        val rotateAnim = RotateAnimation(
            0f, mSpinRevolution + end,
            Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.interpolator = DecelerateInterpolator()
        rotateAnim.repeatCount = 0
        rotateAnim.duration = mSpinDuration
        rotateAnim.setAnimationListener(this)
        rotateAnim.fillAfter = true
        pointerImageView!!.startAnimation(rotateAnim)

        // Play start sound
        spinStartSound.start()
    }

    override fun onAnimationStart(animation: Animation?) {
        infoText!!.text = "Spinning..."
    }

    override fun onAnimationEnd(animation: Animation?) {
        infoText!!.text = ""
        Toast.makeText(this,
            getString(R.string.you_get_a_value_discount_code + prizes[prizeIndex]), Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(animation: Animation?) {}

    private inner class PowerTouchListener : View.OnTouchListener {
        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            when (motionEvent!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    flag = true
                    count = 0
                    Thread {
                        while (flag) {
                            count++
                            if (count == 100) {
                                try {
                                    Thread.sleep(100)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                                count = 0
                            }
                            try {
                                Thread.sleep(10)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }.start()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    flag = false
                    if (spinCount > 0) {
                        startSpinner()
                        hasWatchedAd = true
                    } else {
                        startSpinner()
                        hasWatchedAd = true
                    }
                    spinCount++
                    return false
                }
            }
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        spinStartSound.release()
    }
}
