package com.example.smartvehicleservice.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.smartvehicleservice.databinding.ActivityMainBinding
import com.example.smartvehicleservice.ui.auth.LoginActivity
import com.example.smartvehicleservice.ui.customer.CustomerListFragment
import com.example.smartvehicleservice.ui.vehicle.VehicleListFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(CustomerListFragment())

        // Toolbar's own menu handles Logout -> no need for an ActionBar (theme is NoActionBar).
        binding.toolbar.inflateMenu(com.example.smartvehicleservice.R.menu.toolbar_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == com.example.smartvehicleservice.R.id.action_logout) {
                confirmLogout()
                true
            } else {
                false
            }
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.smartvehicleservice.R.id.nav_customers -> {
                    setFragment(CustomerListFragment()); true
                }
                com.example.smartvehicleservice.R.id.nav_vehicles -> {
                    setFragment(VehicleListFragment()); true
                }
                com.example.smartvehicleservice.R.id.nav_reports -> {
                    setFragment(ReportsFragment()); true
                }
                else -> false
            }
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Log out?")
            .setMessage("You'll need to log in again to access your data.")
            .setPositiveButton("Log out") { _, _ -> logout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        // Clear the whole back stack so the user can't press Back into the app
        // after logging out.
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(com.example.smartvehicleservice.R.id.fragmentContainer, fragment)
            .commit()
    }
}
