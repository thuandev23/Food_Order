package com.example.food_ordering.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.food_ordering.LoginActivity
import com.example.food_ordering.MyVoucherActivity
import com.example.food_ordering.PolicesActivity
import com.example.food_ordering.R
import com.example.food_ordering.databinding.FragmentAboutUserBinding
import com.example.food_ordering.databinding.FragmentProfileBinding
import com.example.food_ordering.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AboutUserFragment : Fragment() {
    private lateinit var binding: FragmentAboutUserBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutUserBinding.inflate(inflater, container, false)
        setUserData()

        binding.accInformation.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.policies.setOnClickListener {
            findNavController().navigate(R.id.policesActivity)
        }
        binding.myVoucher.setOnClickListener {
            findNavController().navigate(R.id.myVoucherActivity)
        }
        binding.feedback.setOnClickListener {

        }
        binding.devNotice.setOnClickListener {

        }
        binding.logout.setOnClickListener {
            if (auth.currentUser != null) {
                auth.addAuthStateListener { firebaseAuth ->
                    if (firebaseAuth.currentUser == null) {
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                    }
                }
                auth.signOut()
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        return binding.root
    }



    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserModel::class.java)
                    if (userProfile != null) {
                        binding.nameUser.setText(userProfile?.name ?: "Please update Information")
                        binding.phoneUser.setText(userProfile.phone ?: "Please update Information")
                        val imagePath2 = "user_images/$userId.jpg"
                        loadImageFromFirebaseStorage(imagePath2)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun loadImageFromFirebaseStorage(imagePath: String) {
        val storageRef = FirebaseStorage.getInstance().getReference(imagePath)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            if (isAdded && !requireActivity().isFinishing) {
                Glide.with(requireContext()).load(uri).into(binding.profileImage)
            }
        }
            .addOnFailureListener { exception ->
            Log.e("ProfileFragment", "Failed to download image: ${exception.message}")
        }
    }
}