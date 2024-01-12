package com.example.food_ordering

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.food_ordering.databinding.ActivityMainBinding
import com.example.food_ordering.fragment.DialogUpdateUserFragment
import com.example.food_ordering.fragment.NotificationBottomFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isUpdateDialogShown = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        binding.notification.setOnClickListener {
            val bottomSheetDialog = NotificationBottomFragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }

        if (!isUpdateDialogShown) {
            val updateInfoDialog = DialogUpdateUserFragment()
            updateInfoDialog.show(supportFragmentManager, "UpdateInfoDialog")
            isUpdateDialogShown = true
        }

    }
}
