package com.example.smartvehicleservice.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartvehicleservice.data.entity.ServiceRecord
import com.example.smartvehicleservice.databinding.ItemServiceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ServiceAdapter : ListAdapter<ServiceRecord, ServiceAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val record = getItem(position)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.binding.tvServiceType.text = record.serviceType
        holder.binding.tvServiceDate.text = sdf.format(Date(record.serviceDate))
        holder.binding.tvServiceNotes.text = record.notes
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ServiceRecord>() {
            override fun areItemsTheSame(a: ServiceRecord, b: ServiceRecord) = a.id == b.id
            override fun areContentsTheSame(a: ServiceRecord, b: ServiceRecord) = a == b
        }
    }
}
