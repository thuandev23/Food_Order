package com.example.food_ordering.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.food_ordering.R

class DialogUpdateUserFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Cập nhật thông tin")
            .setMessage("Vui lòng cập nhật thông tin trước khi mua hàng.")
            .setPositiveButton("Cập nhật") { dialog, _ ->
                dialog.dismiss()
                val navController = findNavController()
                navController.navigate(R.id.profileFragment)
            }
            .setNegativeButton("Để sau") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}