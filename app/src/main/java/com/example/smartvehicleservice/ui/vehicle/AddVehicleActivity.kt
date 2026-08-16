package com.example.smartvehicleservice.ui.vehicle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.data.entity.Customer
import com.example.smartvehicleservice.data.entity.Vehicle
import com.example.smartvehicleservice.data.repository.CustomerRepository
import com.example.smartvehicleservice.data.repository.VehicleRepository
import com.example.smartvehicleservice.databinding.ActivityAddVehicleBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

/**
 * Doubles as both "Add Vehicle" and "Edit Vehicle": if a vehicleId is passed
 * in via the intent extra, the screen loads that vehicle's data, pre-fills
 * the form (including photo preview and GPS coordinates already captured),
 * and updates it on save instead of creating a new record.
 */
class AddVehicleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVehicleBinding
    private lateinit var vehicleRepository: VehicleRepository
    private lateinit var customerRepository: CustomerRepository

    private var customers: List<Customer> = emptyList()
    private var capturedPhotoPath: String? = null
    private var capturedLat: Double? = null
    private var capturedLng: Double? = null
    private var editingVehicle: Vehicle? = null
    private var pendingCustomerIdToSelect: String? = null

    // --- Launches our own CameraX screen (CameraCaptureActivity) and receives the saved photo path back ---
    private val cameraCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val path = result.data?.getStringExtra("photoPath")
            if (path != null) {
                capturedPhotoPath = path
                binding.ivPreview.load(File(path))
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show()
    }

    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) fetchLocation() else Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(this)
        vehicleRepository = VehicleRepository(db.vehicleDao())
        customerRepository = CustomerRepository(db.customerDao())

        val vehicleId = intent.getStringExtra("vehicleId")
        if (vehicleId != null) {
            title = "Edit Vehicle"
            binding.btnSaveVehicle.text = "Update Vehicle"
        } else {
            title = "Add Vehicle"
        }

        loadCustomersIntoSpinner(vehicleId)

        binding.btnCapturePhoto.setOnClickListener { checkCameraPermissionAndCapture() }
        binding.btnCaptureLocation.setOnClickListener { checkLocationPermissionAndFetch() }
        binding.btnSaveVehicle.setOnClickListener { save() }
    }

    private fun loadCustomersIntoSpinner(vehicleIdToEdit: String?) {
        lifecycleScope.launch {
            customers = customerRepository.getAll().first()
            val names = customers.map { it.name }
            binding.spinnerCustomer.adapter = ArrayAdapter(
                this@AddVehicleActivity, android.R.layout.simple_spinner_dropdown_item, names
            )

            if (vehicleIdToEdit != null) {
                loadVehicle(vehicleIdToEdit)
            }
        }
    }

    private fun loadVehicle(id: String) {
        lifecycleScope.launch {
            val vehicle = vehicleRepository.getById(id) ?: return@launch
            editingVehicle = vehicle

            binding.etPlateNumber.setText(vehicle.plateNumber)
            binding.etModel.setText(vehicle.model)

            val customerIndex = customers.indexOfFirst { it.id == vehicle.customerId }
            if (customerIndex >= 0) {
                binding.spinnerCustomer.setSelection(customerIndex)
            }

            vehicle.photoPath?.let { path ->
                capturedPhotoPath = path
                binding.ivPreview.load(File(path))
            }

            if (vehicle.latitude != null && vehicle.longitude != null) {
                capturedLat = vehicle.latitude
                capturedLng = vehicle.longitude
                binding.tvLocation.text = "Garage location: ${vehicle.latitude}, ${vehicle.longitude}"
            }
        }
    }

    // ---------- CameraX / Photo capture ----------
    private fun checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        cameraCaptureLauncher.launch(Intent(this, CameraCaptureActivity::class.java))
    }

    // ---------- GPS / Location capture ----------
    private fun checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        binding.tvLocation.text = "Garage location: fetching GPS fix..."

        // getCurrentLocation() actively requests a fresh GPS fix instead of
        // relying on a cached "last known location" (which is often empty on
        // a device/emulator that hasn't used GPS recently -> that was causing
        // "Could not get location").
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    capturedLat = location.latitude
                    capturedLng = location.longitude
                    binding.tvLocation.text = "Garage location: ${location.latitude}, ${location.longitude}"
                } else {
                    binding.tvLocation.text = "Garage location: not captured"
                    Toast.makeText(
                        this,
                        "Could not get a GPS fix. Make sure Location is ON in phone Settings and try again outdoors.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener {
                binding.tvLocation.text = "Garage location: not captured"
                Toast.makeText(this, "Location request failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ---------- Save ----------
    private fun save() {
        val plate = binding.etPlateNumber.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val position = binding.spinnerCustomer.selectedItemPosition

        if (plate.isEmpty() || model.isEmpty() || customers.isEmpty() || position < 0) {
            Toast.makeText(this, "Fill in all fields and select a customer", Toast.LENGTH_SHORT).show()
            return
        }

        // If editing, keep the same id so Room's REPLACE strategy updates the
        // existing row instead of creating a duplicate, and re-flag it for sync.
        val existing = editingVehicle
        val vehicle = if (existing != null) {
            existing.copy(
                customerId = customers[position].id,
                plateNumber = plate,
                model = model,
                photoPath = capturedPhotoPath,
                latitude = capturedLat,
                longitude = capturedLng,
                isSynced = false
            )
        } else {
            Vehicle(
                customerId = customers[position].id,
                plateNumber = plate,
                model = model,
                photoPath = capturedPhotoPath,
                latitude = capturedLat,
                longitude = capturedLng
            )
        }

        lifecycleScope.launch {
            vehicleRepository.save(vehicle)
            val message = if (existing != null) "Vehicle updated" else "Vehicle saved"
            Toast.makeText(this@AddVehicleActivity, message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
