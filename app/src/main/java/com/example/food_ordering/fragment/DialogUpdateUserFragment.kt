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
            .setTitle(getString(R.string.update_information))
            .setMessage(getString(R.string.please_update_your_information_to_continue_using_buying_food))
            .setPositiveButton(getString(R.string.update)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.action_homeFragment_to_aboutUserFragment)
            }
            .setNegativeButton(getString(R.string.later)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.action_homeFragment_to_aboutUserFragment)
            }
            .create()
    }
}
