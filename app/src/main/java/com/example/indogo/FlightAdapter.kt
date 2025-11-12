package com.example.indogo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.indogo.R
import java.text.NumberFormat
import java.util.Locale

class FlightAdapter(
    private var flights: List<Flight>, // Change from 'val' to 'var' to make it mutable
    private val onFlightClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAirlineLogo: ImageView = itemView.findViewById(R.id.ivAirlineLogo)
        val tvAirlineName: TextView = itemView.findViewById(R.id.tvAirlineName)
        val tvDepartureCodeItem: TextView = itemView.findViewById(R.id.tvDepartureCodeItem)
        val tvDepartureTime: TextView = itemView.findViewById(R.id.tvDepartureTime)
        val tvArrivalCodeItem: TextView = itemView.findViewById(R.id.tvArrivalCodeItem)
        val tvArrivalTime: TextView = itemView.findViewById(R.id.tvArrivalTime)
        val tvFlightDuration: TextView = itemView.findViewById(R.id.tvFlightDuration)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvFreeMeal: TextView = itemView.findViewById(R.id.tvFreeMeal)
        val tvPromoCode: TextView = itemView.findViewById(R.id.tvPromoCode)


        fun bind(flight: Flight) {
            // Set airline logo and name
            ivAirlineLogo.setImageResource(flight.airlineLogo)
            tvAirlineName.text = flight.airlineName

            // Set departure info
            tvDepartureCodeItem.text = flight.departureCode
            tvDepartureTime.text = flight.departureTime

            // Set arrival info
            tvArrivalCodeItem.text = flight.arrivalCode
            tvArrivalTime.text = flight.arrivalTime

            // Set duration
            tvFlightDuration.text = flight.duration

            // Format and set price
            val formattedPrice = NumberFormat.getNumberInstance(Locale("en", "IN"))
                .format(flight.price)
            tvPrice.text = formattedPrice

            // Show/hide free meal badge
            tvFreeMeal.visibility = if (flight.hasFreeMeal) View.VISIBLE else View.GONE

            // Show/hide promo code
            if (flight.promoCode.isNotEmpty()) {
                tvPromoCode.visibility = View.VISIBLE
                tvPromoCode.text = flight.promoCode

                // Set promo background color
                try {
                    tvPromoCode.setBackgroundColor(Color.parseColor(flight.promoBackgroundColor))
                } catch (e: IllegalArgumentException) {
                    // Fallback to default color if parsing fails
                    tvPromoCode.setBackgroundColor(Color.parseColor("#E8F5E9"))
                }
            } else {
                tvPromoCode.visibility = View.GONE
            }

            // Set click listener
            itemView.setOnClickListener {
                onFlightClick(flight)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size

    // ADD THIS METHOD HERE:
    fun updateFlights(newFlights: List<Flight>) {
        this.flights = newFlights
        notifyDataSetChanged()
    }
}