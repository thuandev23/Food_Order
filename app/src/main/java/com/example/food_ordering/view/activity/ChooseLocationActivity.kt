package com.example.food_ordering.activity

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
import com.example.food_ordering.view.activity.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val client = OkHttpClient()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)||
                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    runOnUiThread {
                        Toast.makeText(this,
                            getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                    }
                    if(isLocationEnabled()) {
                        val result =  fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        )
                        result.addOnCompleteListener {
                            val location = it.result
                            if(location != null) {
                                val latitude = location.latitude
                                val longitude = location.longitude
                                getAddressFromLatLng(latitude, longitude)
                            }
                        }
                    }
                    else {
                        Toast.makeText(this,
                            getString(R.string.please_enable_location), Toast.LENGTH_SHORT).show()
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
                return@setOnClickListener
            }else {
                saveAddressUser(address)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        val apiKey = getString(R.string.maps_api_key)
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val jsonObject = JSONObject(body!!)
                val results = jsonObject.getJSONArray("results")
                if(results.length() > 0) {
                    val result = results.getJSONObject(0)
                    val address = result.getString("formatted_address")
                    runOnUiThread {
                        binding.listOfLocation.setText(address)
                    }
                }
            }
        })
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
        task.addOnFailureListener { e->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this, 100)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE)
                as LocationManager
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    private fun saveAddressUser(address: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val addressReference = database.getReference("accounts").child("users").child(userId).child("address")
            addressReference.setValue(address).addOnCompleteListener {
                if(it.isSuccessful) {
                    Log.d("TAG", "saveAddressUser: success to save address")
                }
                else {
                    Toast.makeText(this,
                        getString(R.string.failed_to_save_address), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}