package com.example.food_ordering.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.example.food_ordering.R
import com.example.food_ordering.databinding.ActivityChooseLocationBinding
import com.example.food_ordering.model.UserModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val client = OkHttpClient()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                    }
                    if (isLocationEnabled()) {
                        getCurrentLocation()
                    } else {
                        Toast.makeText(this, getString(R.string.please_enable_location), Toast.LENGTH_SHORT).show()
                        createLocationRequest()
                    }
                }
                else -> {
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.currentLocation.setOnClickListener {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        binding.txtNextHome.setOnClickListener {
            val address = binding.listOfLocation.text.toString()
            if (address.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_address), Toast.LENGTH_SHORT).show()
            } else {
                saveAddressUser(address)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        CoroutineScope(Dispatchers.Main).launch {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                getAddressFromLatLng(latitude, longitude)
            }
        }
    }

    private suspend fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        withContext(Dispatchers.IO) {
            val apiKey = getString(R.string.maps_api_key)
            val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val jsonObject = JSONObject(body!!)
                val results = jsonObject.getJSONArray("results")
                if (results.length() > 0) {
                    val result = results.getJSONObject(0)
                    val address = result.getString("formatted_address")
                    withContext(Dispatchers.Main) {
                        binding.listOfLocation.setText(address)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.location_enabled), Toast.LENGTH_SHORT).show()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@ChooseLocationActivity, 100)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveAddressUser(address: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val addressReference = database.getReference("accounts").child("users").child(userId).child("address")
            addressReference.setValue(address).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "saveAddressUser: success to save address")
                } else {
                    Toast.makeText(this, getString(R.string.failed_to_save_address), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}