package com.example.smartvehicleservice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.databinding.FragmentReportsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Real dashboard: live counts of customers, vehicles, service records and
 * anything still waiting to sync to Firebase, plus the next few upcoming
 * service reminders — all pulled straight from Room.
 */
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    private fun loadStats() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            // getAll() returns a Flow, so take a single snapshot via first()
            val customerList = db.customerDao().getAll().first()
            val vehicleList = db.vehicleDao().getAll().first()
            val serviceTotal = db.serviceDao().countAll()

            val pendingSync = db.customerDao().getUnsynced().size +
                    db.vehicleDao().getUnsynced().size +
                    db.serviceDao().getUnsynced().size

            binding.tvCustomerCount.text = customerList.size.toString()
            binding.tvVehicleCount.text = vehicleList.size.toString()
            binding.tvServiceCount.text = serviceTotal.toString()
            binding.tvPendingSyncCount.text = pendingSync.toString()

            // Upcoming reminders (next 30 days, soonest first)
            val now = System.currentTimeMillis()
            val in30Days = now + 30L * 24 * 60 * 60 * 1000
            val due = db.serviceDao().getDueReminders(in30Days)
                .filter { it.nextReminderDate != null }
                .sortedBy { it.nextReminderDate }
                .take(5)

            if (due.isEmpty()) {
                binding.tvUpcomingList.text = "No upcoming reminders in the next 30 days."
            } else {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                binding.tvUpcomingList.text = due.joinToString("\n") { record ->
                    "• ${record.serviceType} — due ${sdf.format(Date(record.nextReminderDate!!))}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
