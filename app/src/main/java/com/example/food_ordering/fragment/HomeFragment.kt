package com.example.food_ordering.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.food_ordering.R
import com.example.food_ordering.adapter.PopularAdapter
import com.example.food_ordering.adapter.VoucherAdapter
import com.example.food_ordering.databinding.FragmentHomeBinding
import com.example.food_ordering.model.AllVoucher
import com.example.food_ordering.model.CartItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var popularItems: MutableList<CartItem>
    private lateinit var voucherItems: MutableList<AllVoucher>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.txtViewMenu.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        retrievePopular()
        retrieveVoucher()
        return binding.root
    }

    private fun retrieveVoucher() {
        database = FirebaseDatabase.getInstance()
        val voucherRef:DatabaseReference = database.reference.child("voucher")
        voucherItems = mutableListOf()
        voucherRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                voucherItems.clear()
                for (voucherSnapshot in snapshot.children) {
                    val voucherItem = voucherSnapshot.getValue(AllVoucher::class.java)
                    voucherItem?.let { voucherItems.add(it) }
                }
                setVoucherAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setVoucherAdapter() {
        val adapter = VoucherAdapter(voucherItems, requireContext())
        binding.voucherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.voucherRecyclerView.adapter = adapter
    }

    private fun retrievePopular() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("ProductsSold")
        popularItems = mutableListOf()
        //fetch data in firebase database
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                popularItems.clear()
                for (foodSnapshot in snapshot.children) {
                    val popularItem = foodSnapshot.getValue(CartItem::class.java)
                    popularItem?.let { popularItems.add(it)}
                }
                bigSoldPopularItems()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    private fun bigSoldPopularItems() {
        database = FirebaseDatabase.getInstance()
        val bigSoldRef: DatabaseReference = database.reference.child("ProductsSold")

        bigSoldRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val popularItemsList = mutableListOf<CartItem>()
                for (data in snapshot.children) {
                    val item = data.getValue(CartItem::class.java)
                    item?.let { popularItemsList.add(it) }
                }
                bubbleSort(popularItemsList)
                val subSetMenuItems = popularItemsList.take(6)
                setPopularAdapter(subSetMenuItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching popular items: ${error.message}")
            }
        })
    }

    private fun setPopularAdapter(submenuItems: List<CartItem>) {
        val adapter = PopularAdapter(submenuItems, requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }
    private fun bubbleSort(items: MutableList<CartItem>) {
        val n = items.size
        for(i in 0 until n-1){
            for(j in 0 until n-i-1){
                if(items[j].foodQuantities!! < items[j+1].foodQuantities!!){
                    val temp = items[j]
                    items[j] = items[j+1]
                    items[j+1] = temp
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage  = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

}