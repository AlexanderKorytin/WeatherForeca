package com.example.weatherforeca

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LocationsAdapter(val clickListener: LocationClickListener):
    RecyclerView.Adapter<LocationViewHolder>() {

    var locations = ArrayList<ForecastLocation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder = LocationViewHolder(parent)

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations.get(position))
        holder.itemView.setOnClickListener{ clickListener.onLocationClick(locations.get(position)) }
    }
    fun interface LocationClickListener {
        fun onLocationClick(location: ForecastLocation)
    }
}