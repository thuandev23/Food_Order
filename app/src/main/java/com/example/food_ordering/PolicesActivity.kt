package com.example.food_ordering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class PolicesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polices)

        val btn = findViewById<AppCompatButton>(R.id.btnBackDetails)
        btn.setOnClickListener {
            finish()
        }
    }
}