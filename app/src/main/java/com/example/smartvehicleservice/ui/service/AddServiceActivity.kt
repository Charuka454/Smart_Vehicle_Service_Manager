package com.example.smartvehicleservice.ui.service

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.data.entity.ServiceRecord
import com.example.smartvehicleservice.data.repository.ServiceRepository
import com.example.smartvehicleservice.databinding.ActivityAddServiceBinding
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AddServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddServiceBinding
    private lateinit var repository: ServiceRepository
    private var vehicleId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vehicleId = intent.getStringExtra("vehicleId") ?: ""
        val db = AppDatabase.getInstance(this)
        repository = ServiceRepository(db.serviceDao())

        binding.btnSaveService.setOnClickListener { save() }
    }

    private fun save() {
        val type = binding.etServiceType.text.toString().trim()
        val notes = binding.etServiceNotes.text.toString().trim()
        val reminderDaysText = binding.etNextReminderDays.text.toString().trim()

        if (type.isEmpty()) {
            Toast.makeText(this, "Enter a service type", Toast.LENGTH_SHORT).show()
            return
        }

        val reminderDate = reminderDaysText.toLongOrNull()?.let { days ->
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days)
        }

        val record = ServiceRecord(
            vehicleId = vehicleId,
            serviceType = type,
            notes = notes,
            nextReminderDate = reminderDate
        )

        lifecycleScope.launch {
            repository.save(record)
            Toast.makeText(this@AddServiceActivity, "Service record saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
