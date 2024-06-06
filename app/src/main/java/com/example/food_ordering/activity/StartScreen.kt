package com.example.food_ordering.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.food_ordering.databinding.ActivityStartScreenBinding
import com.example.food_ordering.setLocaleBasedOnRegion


class StartScreen : AppCompatActivity() {

    private val binding: ActivityStartScreenBinding by lazy {
        ActivityStartScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        setLocaleBasedOnRegion(this)


        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnNextStart.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
