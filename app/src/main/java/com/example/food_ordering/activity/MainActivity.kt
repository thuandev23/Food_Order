package com.example.food_ordering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityMainBinding
import com.example.food_ordering.fragment.DialogUpdateUserFragment
import com.example.food_ordering.fragment.FavouriteFragment
import com.example.food_ordering.fragment.NotificationBottomFragment
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.model.UserModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
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
        binding.spin.setOnClickListener {
            startActivity(Intent(this, SpinMiniGameActivity::class.java))
        }
        getUserData()
        getItemFromFavorite()
        binding.countFavorite.setOnClickListener {
            navController.navigate(R.id.favouriteFragment)
        }

        binding.favorite.setOnClickListener {
            navController.navigate(R.id.favouriteFragment)
        }
    }

    private fun getItemFromFavorite() {
        val userID = auth.currentUser?.uid
        if (userID != null) {
            val favouriteReference = database.getReference("accounts").child("users").child(userID).child("Favourites")

            favouriteReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favoriteCount = snapshot.childrenCount
                    binding.countFavorite.text = favoriteCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                    Toast.makeText(this@MainActivity, "Failed to retrieve favorites: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun getUserData() {
        val userID = auth.currentUser?.uid
        if (userID != null){
            val userReference = database.getReference("accounts").child("users").child(userID)
            userReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val  userProfile = snapshot.getValue(UserModel::class.java)
                    if (userProfile != null){
                        if(userProfile.name == ""|| userProfile.address == ""|| userProfile.email == ""|| userProfile.phone == "") {
                                val updateInfoDialog = DialogUpdateUserFragment()
                                updateInfoDialog.show(supportFragmentManager.beginTransaction(), "UpdateInfoDialog")
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}
