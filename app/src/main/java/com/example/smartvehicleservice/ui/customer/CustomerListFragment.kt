package com.example.smartvehicleservice.ui.customer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.data.repository.CustomerRepository
import com.example.smartvehicleservice.databinding.FragmentCustomersBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CustomerListFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: CustomerRepository
    private lateinit var adapter: CustomerAdapter
    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())
        repository = CustomerRepository(db.customerDao())

        adapter = CustomerAdapter(
            onClick = { customer ->
                val intent = Intent(requireContext(), AddCustomerActivity::class.java)
                intent.putExtra("customerId", customer.id)
                startActivity(intent)
            },
            onLongClick = { customer -> confirmDelete(customer) }
        )
        binding.rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomers.adapter = adapter

        loadCustomers("")

        binding.etSearchCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadCustomers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAddCustomer.setOnClickListener {
            startActivity(Intent(requireContext(), AddCustomerActivity::class.java))
        }
    }

    private fun loadCustomers(query: String) {
        loadJob?.cancel()
        val flow = if (query.isBlank()) repository.getAll() else repository.search(query)
        loadJob = lifecycleScope.launch {
            flow.collect { list -> adapter.submitList(list) }
        }
    }

    private fun confirmDelete(customer: com.example.smartvehicleservice.data.entity.Customer) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete customer?")
            .setMessage("This removes ${customer.name} from this device. It won't remove data already synced to Firebase.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch { repository.delete(customer) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
