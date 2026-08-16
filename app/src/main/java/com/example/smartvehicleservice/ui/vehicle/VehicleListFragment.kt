package com.example.smartvehicleservice.ui.vehicle

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
import com.example.smartvehicleservice.data.repository.VehicleRepository
import com.example.smartvehicleservice.databinding.FragmentVehiclesBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VehicleListFragment : Fragment() {

    private var _binding: FragmentVehiclesBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: VehicleRepository
    private lateinit var adapter: VehicleAdapter
    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehiclesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())
        repository = VehicleRepository(db.vehicleDao())

        adapter = VehicleAdapter(
            onClick = { vehicle ->
                val intent = Intent(requireContext(), VehicleDetailActivity::class.java)
                intent.putExtra("vehicleId", vehicle.id)
                startActivity(intent)
            },
            onLongClick = { vehicle -> confirmDelete(vehicle) }
        )
        binding.rvVehicles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehicles.adapter = adapter

        loadVehicles("")

        binding.etSearchVehicle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadVehicles(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAddVehicle.setOnClickListener {
            startActivity(Intent(requireContext(), AddVehicleActivity::class.java))
        }
    }

    private fun loadVehicles(query: String) {
        loadJob?.cancel()
        val flow = if (query.isBlank()) repository.getAll() else repository.search(query)
        loadJob = lifecycleScope.launch {
            flow.collect { list -> adapter.submitList(list) }
        }
    }

    private fun confirmDelete(vehicle: com.example.smartvehicleservice.data.entity.Vehicle) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete vehicle?")
            .setMessage("This removes ${vehicle.plateNumber} and its local data from this device.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch { repository.delete(vehicle) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
