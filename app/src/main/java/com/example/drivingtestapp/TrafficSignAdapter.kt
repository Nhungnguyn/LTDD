package com.example.drivingtestapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drivingtestapp.databinding.ItemTrafficSignBinding
import com.squareup.picasso.Picasso

class TrafficSignAdapter(private val trafficSigns: List<TrafficSign>) :
    RecyclerView.Adapter<TrafficSignAdapter.TrafficSignViewHolder>() {

    class TrafficSignViewHolder(val binding: ItemTrafficSignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficSignViewHolder {
        val binding = ItemTrafficSignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrafficSignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrafficSignViewHolder, position: Int) {
        val trafficSign = trafficSigns[position]
        holder.binding.tvDescription.text = trafficSign.description
        Picasso.get().load(trafficSign.image_url).into(holder.binding.ivSignImage)
    }

    override fun getItemCount(): Int = trafficSigns.size
}