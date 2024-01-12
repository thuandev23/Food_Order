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
            val name = binding.name.text.toString()
            val address = binding.address.text.toString()
            val email = binding.email.text.toString()
            val phone = binding.phone.text.toString()

            updateUserData(name, address, email, phone)
        }
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun updateUserData(name: String, address: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid

        if (userId != null && imageUser != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("user_images/$userId.jpg")

            val uploadTask = imageRef.putFile(imageUser!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnCompleteListener { downloadTask ->
                    if (downloadTask.isSuccessful) {
                        val imageUrl = downloadTask.result.toString()

                        val userReference = database.getReference("user").child(userId)
                        val userData = hashMapOf(
                            "name" to binding.name.text.toString(),
                            "address" to binding.address.text.toString(),
                            "email" to binding.email.text.toString(),
                            "phone" to binding.phone.text.toString(),
                            "image" to imageUrl
                        )

                        userReference.setValue(userData).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Profile update successfully !",
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
        } else {
            Log.e("ProfileFragment", "Error: userId or imageUser is null")
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

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