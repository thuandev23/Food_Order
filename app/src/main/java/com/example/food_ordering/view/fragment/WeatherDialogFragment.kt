package com.example.food_ordering.view.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.food_ordering.R
import com.example.food_ordering.databinding.FragmentWeatherDialogBinding
import com.example.food_ordering.model.RetrofitInstance
import com.example.food_ordering.model.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlin.math.round

class WeatherDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentWeatherDialogBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherDialogBinding.inflate(layoutInflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return binding.root
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                fetchWeather(it.latitude, it.longitude)
            }
        }
        return binding.root
    }

    private fun fetchWeather(latitude: Double, longitude: Double) {
        val weatherService = RetrofitInstance.instance.create(WeatherApi::class.java)
        val apiKey = getString(R.string._api)

        lifecycleScope.launch {
            try {
                val response = weatherService.getCurrentWeather(latitude, longitude, apiKey)
                val temperature = round(response.main.temp).toInt()
                val foodSuggestion = when {
                    temperature > 29 -> getString(R.string.it_s_quite_hot_today_how_about_a_nice_cool_dry_dish)
                    temperature < 20 -> getString(R.string.it_s_chilly_outside_a_warm_dish_with_broth_would_be_perfect)
                    else -> getString(R.string.the_weather_is_moderate_you_can_enjoy_either_a_dish_with_broth_or_a_dry_dish)
                }

                /*AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.weather))
                    .setMessage(getString(R.string.current_temperature)+"$temperature"+ getString(R.string.c_weather)+" $temperature"+ getString(R.string.suggestion)+ foodSuggestion)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()*/
                binding.txtWeather.text = getString(R.string.current_temperature)+"$temperature"+ getString(R.string.c_weather)+ getString(R.string.suggestion)+ foodSuggestion
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to get weather data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}