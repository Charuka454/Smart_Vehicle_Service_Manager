package com.example.smartvehicleservice.ui.customer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.data.entity.Customer
import com.example.smartvehicleservice.data.repository.CustomerRepository
import com.example.smartvehicleservice.databinding.ActivityAddCustomerBinding
import kotlinx.coroutines.launch

/**
 * Doubles as both "Add Customer" and "Edit Customer":
 * if a customerId is passed in via the intent extra, the screen loads that
 * customer's data, pre-fills the form, and updates it on save instead of
 * creating a new record.
 */
class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private lateinit var repository: CustomerRepository
    private var editingCustomer: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(this)
        repository = CustomerRepository(db.customerDao())

        val customerId = intent.getStringExtra("customerId")
        if (customerId != null) {
            title = "Edit Customer"
            binding.btnSaveCustomer.text = "Update Customer"
            loadCustomer(customerId)
        } else {
            title = "Add Customer"
        }

        binding.btnSaveCustomer.setOnClickListener { save() }
    }

    private fun loadCustomer(id: String) {
        lifecycleScope.launch {
            val customer = repository.getById(id)
            if (customer != null) {
                editingCustomer = customer
                binding.etName.setText(customer.name)
                binding.etPhone.setText(customer.phone)
                binding.etAddress.setText(customer.address)
            }
        }
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and phone are required", Toast.LENGTH_SHORT).show()
            return
        }

        // If editing, keep the same id so Room's REPLACE strategy updates the
        // existing row instead of creating a duplicate. A successful edit also
        // resets isSynced to false so the change gets pushed to Firestore again.
        val existing = editingCustomer
        val customer = if (existing != null) {
            existing.copy(name = name, phone = phone, address = address, isSynced = false)
        } else {
            Customer(name = name, phone = phone, address = address)
        }

        lifecycleScope.launch {
            repository.save(customer)
            val message = if (existing != null) "Customer updated" else "Customer saved"
            Toast.makeText(this@AddCustomerActivity, message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
