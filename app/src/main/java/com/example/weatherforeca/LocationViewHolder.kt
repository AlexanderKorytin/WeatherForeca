package com.example.weatherforeca

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationViewHolder(parent: ViewGroup):RecyclerView
.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.location_view, parent, false)) {

    val name: TextView = itemView.findViewById<TextView>(R.id.locationName)

    fun bind(location:ForecastLocation){
        name.text = "${location.name} (${location.country})"
    }
}