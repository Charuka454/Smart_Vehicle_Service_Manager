package com.example.smartvehicleservice.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartvehicleservice.data.entity.Customer
import com.example.smartvehicleservice.databinding.ItemCustomerBinding

class CustomerAdapter(
    private val onClick: (Customer) -> Unit,
    private val onLongClick: (Customer) -> Unit = {}
) : ListAdapter<Customer, CustomerAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val customer = getItem(position)
        holder.binding.tvCustomerName.text = customer.name
        holder.binding.tvCustomerPhone.text = customer.phone
        holder.binding.tvSyncStatus.text = if (customer.isSynced) "Synced ✓" else "Pending sync…"
        holder.itemView.setOnClickListener { onClick(customer) }
        holder.itemView.setOnLongClickListener { onLongClick(customer); true }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Customer>() {
            override fun areItemsTheSame(a: Customer, b: Customer) = a.id == b.id
            override fun areContentsTheSame(a: Customer, b: Customer) = a == b
        }
    }
}
