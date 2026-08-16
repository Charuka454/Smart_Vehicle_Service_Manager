package com.example.smartvehicleservice.ui.vehicle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.smartvehicleservice.data.entity.Vehicle
import com.example.smartvehicleservice.databinding.ItemVehicleBinding
import java.io.File

class VehicleAdapter(
    private val onClick: (Vehicle) -> Unit,
    private val onLongClick: (Vehicle) -> Unit = {}
) : ListAdapter<Vehicle, VehicleAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemVehicleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val vehicle = getItem(position)
        holder.binding.tvPlateNumber.text = vehicle.plateNumber
        holder.binding.tvVehicleModel.text = vehicle.model
        holder.binding.tvOwnerName.text = if (vehicle.isSynced) "Synced ✓" else "Pending sync…"
        vehicle.photoPath?.let { path ->
            holder.binding.ivVehiclePhoto.load(File(path))
        }
        holder.itemView.setOnClickListener { onClick(vehicle) }
        holder.itemView.setOnLongClickListener { onLongClick(vehicle); true }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Vehicle>() {
            override fun areItemsTheSame(a: Vehicle, b: Vehicle) = a.id == b.id
            override fun areContentsTheSame(a: Vehicle, b: Vehicle) = a == b
        }
    }
}
