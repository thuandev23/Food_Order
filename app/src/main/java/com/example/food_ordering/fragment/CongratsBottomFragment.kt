package com.example.food_ordering.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.food_ordering.MainActivity
import com.example.food_ordering.R
import com.example.food_ordering.databinding.FragmentCongratsBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CongratsBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCongratsBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCongratsBottomBinding.inflate(inflater, container, false)

        binding.btnGoHome.setOnClickListener{
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object {}
}