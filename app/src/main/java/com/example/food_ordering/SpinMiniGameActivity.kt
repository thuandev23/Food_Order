package com.example.food_ordering
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class SpinMiniGameActivity : AppCompatActivity(), Animation.AnimationListener {
    private var count = 0
    private var flag = false
    private var powerButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_mini_game)
        /**get Id*/
        powerButton = findViewById(R.id.powerButton)
        powerButton!!.setOnTouchListener(PowerTouchListener())
        intSpinner()

        var btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    /**
     * All the vars you need
     * */

    val prizes = intArrayOf(20000, 15000, 60000, 50000, 10000, 12000, 30000, 18000, 40000, 30000, 16000, 25000)
    private var mSpinDuration: Long = 0
    private var mSpinRevolution = 0f
    var pointerImageView: ImageView? = null
    var infoText: TextView? = null
    var prizeText = "N/A"

    // Final point of rotation defined right here
    val end = Math.floor(Math.random() * 3600).toInt() // random : 0-360
    val numOfPrizes = prizes.size // quantity of prize
    val degreesPerPrize = 360 / numOfPrizes // size of sector per prize in degrees
    val shift = 0 // shit where the arrow points
    val prizeIndex = (shift + end) % numOfPrizes

    private fun intSpinner() {
        pointerImageView = findViewById(R.id.imageWheel)
        infoText = findViewById(R.id.infoText)
    }
    fun startSpinner() {
        mSpinRevolution = 3600f
        mSpinDuration = 5000

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

    }

    override fun onAnimationStart(p0: Animation?) {
        infoText!!.text = "Spinning..."
    }

    override fun onAnimationEnd(p0: Animation?) {
        infoText!!.text = ""
        Toast.makeText(this, "You get a value discount code : ${prizes[prizeIndex]}", Toast.LENGTH_SHORT).show()
    }

    override fun onAnimationRepeat(p0: Animation?) {}

    private inner class PowerTouchListener : View.OnTouchListener {
        override fun onTouch(p0: View?, motionEvent: MotionEvent?): Boolean {

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
                    startSpinner()
                    return false
                }

            }
            return false
        }

    }
}