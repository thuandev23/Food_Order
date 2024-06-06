package com.example.food_ordering.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.food_ordering.R
import com.example.food_ordering.databinding.FragmentProfileBinding
import com.example.food_ordering.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var imageUser: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profileImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        setUserData()
        binding.btnSaveInformation.setOnClickListener {
            updateUserData()
        }
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun updateUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null && imageUser != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("user_images/$userId.jpg")
            val uploadTask = imageRef.putFile(imageUser!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    val name = binding.name.text.toString()
                    val address = binding.address.text.toString()
                    val email = binding.email.text.toString()
                    val phone = binding.phone.text.toString()

                    val userReference = database.getReference("accounts").child("users").child(userId)
                    val userData = hashMapOf(
                        "name" to name,
                        "address" to address,
                        "email" to email,
                        "phone" to phone,
                        "image" to imageUrl
                    )
                    userReference.setValue(userData).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.profile_update_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Profile update failed !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("accounts").child("users").child(userId)

            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserModel::class.java)
                    if (userProfile != null) {
                        binding.name.setText(userProfile.name)
                        binding.address.setText(userProfile.address)
                        binding.email.setText(userProfile.email)
                        binding.phone.setText(userProfile.phone)
                        //image
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
        if (isAdded) {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this).load(uri).into(binding.profileImage)
            }.addOnFailureListener { exception ->
                Log.e("ProfileFragment", "Failed to download image: ${exception.message}")
            }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.profileImage.setImageURI(uri)
            imageUser = uri
        }
    }
}