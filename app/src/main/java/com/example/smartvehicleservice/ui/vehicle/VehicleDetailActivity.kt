package com.example.smartvehicleservice.ui.vehicle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.data.repository.ServiceRepository
import com.example.smartvehicleservice.data.repository.VehicleRepository
import com.example.smartvehicleservice.databinding.ActivityVehicleDetailBinding
import com.example.smartvehicleservice.ui.service.AddServiceActivity
import com.example.smartvehicleservice.ui.service.ServiceAdapter
import kotlinx.coroutines.launch
import java.io.File

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehicleDetailBinding
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var adapter: ServiceAdapter
    private var vehicleId: String = ""
    private var vehicleLat: Double? = null
    private var vehicleLng: Double? = null
    private var vehiclePlate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vehicleId = intent.getStringExtra("vehicleId") ?: ""

        val db = AppDatabase.getInstance(this)
        vehicleRepository = VehicleRepository(db.vehicleDao())
        serviceRepository = ServiceRepository(db.serviceDao())

        adapter = ServiceAdapter()
        binding.rvServiceHistory.layoutManager = LinearLayoutManager(this)
        binding.rvServiceHistory.adapter = adapter

        loadVehicle()
        loadServiceHistory()

        binding.fabAddService.setOnClickListener {
            val intent = Intent(this, AddServiceActivity::class.java)
            intent.putExtra("vehicleId", vehicleId)
            startActivity(intent)
        }

        binding.btnEditVehicle.setOnClickListener {
            val intent = Intent(this, AddVehicleActivity::class.java)
            intent.putExtra("vehicleId", vehicleId)
            startActivity(intent)
        }

        binding.btnViewOnMap.setOnClickListener { openLocationOnMap() }
    }

    override fun onResume() {
        super.onResume()
        loadServiceHistory() // refresh in case a new record was just added
        loadVehicle() // refresh in case an edit just changed the location/photo
    }

    private fun loadVehicle() {
        lifecycleScope.launch {
            val vehicle = vehicleRepository.getById(vehicleId) ?: return@launch
            vehiclePlate = vehicle.plateNumber
            binding.tvDetailPlate.text = "${vehicle.plateNumber} • ${vehicle.model}"
            vehicle.photoPath?.let { path -> binding.ivDetailPhoto.load(File(path)) }

            vehicleLat = vehicle.latitude
            vehicleLng = vehicle.longitude

            // Only makes sense to offer "View on Map" once a location was actually captured.
            binding.btnViewOnMap.isEnabled = vehicleLat != null && vehicleLng != null
            binding.btnViewOnMap.alpha = if (binding.btnViewOnMap.isEnabled) 1f else 0.5f
        }
    }

    /**
     * Opens the device's default Maps app (Google Maps or any other installed
     * maps app) with a pin dropped at this vehicle's saved garage location.
     * Using an implicit "geo:" intent instead of embedding the Maps SDK means
     * no Google Maps API key setup is required, so this works reliably out
     * of the box on any Android device.
     */
    private fun openLocationOnMap() {
        val lat = vehicleLat
        val lng = vehicleLng
        if (lat == null || lng == null) {
            Toast.makeText(this, "No location captured for this vehicle yet", Toast.LENGTH_SHORT).show()
            return
        }

        val label = Uri.encode(vehiclePlate.ifBlank { "Vehicle location" })
        val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "No maps app found on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadServiceHistory() {
        lifecycleScope.launch {
            serviceRepository.getByVehicle(vehicleId).collect { list ->
                adapter.submitList(list)
            }
        }
    }
}
