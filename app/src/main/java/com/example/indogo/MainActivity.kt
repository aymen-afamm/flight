package com.example.indogo

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var flightAdapter: FlightAdapter
    private var flights: List<Flight> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFlights()
        setupHeader()
        setupRecyclerView()
    }

    private fun setupHeader() {
        // Set current date in header
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())
        findViewById<TextView>(R.id.tvDateHeader).text = currentDate

        // Setup header back button
        findViewById<ImageView>(R.id.btnBackHeader).setOnClickListener {
            finish()
        }

        // Setup header edit button
        findViewById<ImageView>(R.id.btnEditHeader).setOnClickListener {
            Toast.makeText(this, "Edit flight details", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Toast.makeText(this, "Landscape Mode", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                Toast.makeText(this, "Portrait Mode", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvFlightList)

        // Check if we're in tablet mode
        val isTabletMode = findViewById<LinearLayout>(R.id.leftPane) != null

        if (isTabletMode) {
            // LINEAR layout for tablet - ONE TICKET PER LINE
            recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                recyclerView.layoutManager = GridLayoutManager(this, 2)
            } else {
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

        // Set adapter
        flightAdapter = FlightAdapter(flights) { flight ->
            onFlightClick(flight)
        }
        recyclerView.adapter = flightAdapter

        // Setup filter buttons
        setupFilterButtons()
    }

    private fun setupFilterButtons() {
        findViewById<Button>(R.id.btnHighToLow)?.setOnClickListener {
            sortFlights(true)
        }

        findViewById<Button>(R.id.btnLowToHigh)?.setOnClickListener {
            sortFlights(false)
        }

        findViewById<Button>(R.id.btnAirlineType)?.setOnClickListener {
            Toast.makeText(this, "Filter by airline type", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.btnFilter)?.setOnClickListener {
            Toast.makeText(this, "Open filters", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sortFlights(highToLow: Boolean) {
        val sortedFlights = if (highToLow) {
            flights.sortedByDescending { it.price }
        } else {
            flights.sortedBy { it.price }
        }

        flightAdapter.updateFlights(sortedFlights)
    }

    private fun loadFlights() {
        flights = listOf(
            Flight(
                airlineName = "SpiceJet",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "BOM",
                departureTime = "08:20",
                arrivalCode = "DEL",
                arrivalTime = "10:35",
                duration = "02h 15m",
                price = 5450,
                hasFreeMeal = false,
                promoCode = "Use Code : SPICE45 and get 45% instant cashback",
                promoBackgroundColor = "#FFF3E0"
            ),
            Flight(
                airlineName = "Vistara",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "BLR",
                departureTime = "16:45",
                arrivalCode = "HYD",
                arrivalTime = "18:00",
                duration = "01h 15m",
                price = 4200,
                hasFreeMeal = true,
                promoCode = "",
                promoBackgroundColor = "#E8F5E9"
            ),
            Flight(
                airlineName = "Air India",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "MAA",
                departureTime = "13:30",
                arrivalCode = "CCU",
                arrivalTime = "16:15",
                duration = "02h 45m",
                price = 6800,
                hasFreeMeal = true,
                promoCode = "Use Code : AIREXPRESS and get 30% cashback",
                promoBackgroundColor = "#E3F2FD"
            ),
            Flight(
                airlineName = "Indigo",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "GOI",
                departureTime = "07:15",
                arrivalCode = "BOM",
                arrivalTime = "08:00",
                duration = "00h 45m",
                price = 3250,
                hasFreeMeal = false,
                promoCode = "Use Code : 6EFLY and get 25% instant discount",
                promoBackgroundColor = "#FCE4EC"
            ),
            Flight(
                airlineName = "Akasa Air",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "DEL",
                departureTime = "19:20",
                arrivalCode = "BLR",
                arrivalTime = "22:05",
                duration = "02h 45m",
                price = 6100,
                hasFreeMeal = true,
                promoCode = "Use Code : AKASA25 for 25% off",
                promoBackgroundColor = "#E8F5E9"
            ),
            Flight(
                airlineName = "AirAsia",
                airlineLogo = R.drawable.ic_airline_placeholder,
                departureCode = "HYD",
                departureTime = "11:10",
                arrivalCode = "MAA",
                arrivalTime = "12:25",
                duration = "01h 15m",
                price = 3800,
                hasFreeMeal = false,
                promoCode = "Use Code : ASIABIG and get 35% cashback",
                promoBackgroundColor = "#FFF3E0"
            )
        )
    }

    private fun onFlightClick(flight: Flight) {
        // Check if we're in tablet mode
        val isTabletMode = findViewById<LinearLayout>(R.id.leftPane) != null

        if (isTabletMode) {
            // Show ticket details in right pane
            showTicketDetails(flight)
        } else {
            // Open TicketActivity for phones
            val intent = Intent(this, TicketActivity::class.java).apply {
                putExtra("flight", flight)
            }
            startActivity(intent)
        }
    }

    private fun showTicketDetails(flight: Flight) {
        try {
            // Hide empty state
            findViewById<TextView>(R.id.tvEmptyState).visibility = View.GONE

            // Show ticket details
            val ticketDetails = findViewById<View>(R.id.ticketDetails)
            ticketDetails.visibility = View.VISIBLE

            // Update ticket details with flight data
            updateTicketDetails(flight)
        } catch (e: Exception) {
            Toast.makeText(this, "Error showing ticket: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun updateTicketDetails(flight: Flight) {
        try {
            // Update airline name
            findViewById<TextView>(R.id.ivLogo).text = flight.airlineName

            // Update date
            val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())
            findViewById<TextView>(R.id.tvDate).text = currentDate

            // Update departure
            val departureLayout = findViewById<LinearLayout>(R.id.layoutDeparture)
            (departureLayout.getChildAt(0) as? TextView)?.text = flight.departureCode
            (departureLayout.getChildAt(1) as? TextView)?.text = getAirportName(flight.departureCode)

            // Update arrival
            val relativeLayout = departureLayout.parent as? RelativeLayout
            relativeLayout?.let { parent ->
                for (i in 0 until parent.childCount) {
                    val child = parent.getChildAt(i)
                    if (child is LinearLayout &&
                        child.id != R.id.layoutDeparture &&
                        child.id != R.id.layoutFlightInfo) {
                        (child.getChildAt(0) as? TextView)?.text = flight.arrivalCode
                        (child.getChildAt(1) as? TextView)?.text = getAirportName(flight.arrivalCode)
                        break
                    }
                }
            }

            // Update duration
            val flightInfoLayout = findViewById<LinearLayout>(R.id.layoutFlightInfo)
            for (i in 0 until flightInfoLayout.childCount) {
                val child = flightInfoLayout.getChildAt(i)
                if (child is TextView && child.text.contains("h")) {
                    child.text = flight.duration
                    break
                }
            }

            // Update flight code
            findViewById<TextView>(R.id.tvFlightCode).text = generateFlightCode(flight.airlineName)

            // Update boarding time
            findViewById<TextView>(R.id.tvBoardingTime).text = calculateBoardingTime(flight.departureTime)

            // Setup back button for ticket details
            findViewById<ImageView>(R.id.btnBack).setOnClickListener {
                // Hide ticket details and show empty state
                findViewById<View>(R.id.ticketDetails).visibility = View.GONE
                findViewById<TextView>(R.id.tvEmptyState).visibility = View.VISIBLE
            }

            // Setup download button
            findViewById<Button>(R.id.btnDownload).setOnClickListener {
                downloadTicketAsPdf(flight)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error updating ticket: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun downloadTicketAsPdf(flight: Flight) {
        try {
            val pdfGenerator = PdfGenerator(this)
            val passengerName = "Shreya Kumar" // Default passenger name
            val flightCode = generateFlightCode(flight.airlineName)
            val boardingTime = calculateBoardingTime(flight.departureTime)
            val gate = "A5" // Default gate
            val terminal = "T2" // Default terminal
            val seatNumber = "A5" // Default seat

            val pdfFile = pdfGenerator.generateFlightTicketPdf(
                flight = flight,
                passengerName = passengerName,
                flightCode = flightCode,
                boardingTime = boardingTime,
                gate = gate,
                terminal = terminal,
                seatNumber = seatNumber
            )

            Toast.makeText(
                this,
                "E-Ticket downloaded successfully!",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error downloading ticket: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    private fun getAirportName(code: String): String {
        return when (code) {
            "DEL" -> "Delhi International Airport"
            "BLR" -> "Bengaluru Airport India"
            "BOM" -> "Mumbai Chhatrapati Shivaji Airport"
            "MAA" -> "Chennai International Airport"
            "CCU" -> "Kolkata Netaji Subhas Airport"
            "HYD" -> "Hyderabad Rajiv Gandhi Airport"
            else -> "$code Airport"
        }
    }

    private fun generateFlightCode(airlineName: String): String {
        val airlineCode = when (airlineName.lowercase()) {
            "indigo" -> "IG"
            "air india" -> "AI"
            "spicejet" -> "SG"
            "vistara" -> "UK"
            "goair" -> "G8"
            else -> "XX"
        }
        val flightNumber = (1000..9999).random()
        return "$airlineCode-$flightNumber"
    }

    private fun calculateBoardingTime(departureTime: String): String {
        return try {
            val parts = departureTime.split(":")
            if (parts.size == 2) {
                var hour = parts[0].toInt()
                var minute = parts[1].toInt()

                // Boarding time is 45 minutes before departure
                minute -= 45
                if (minute < 0) {
                    minute += 60
                    hour -= 1
                }
                if (hour < 0) hour += 24

                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour > 12 -> hour - 12
                    hour == 0 -> 12
                    else -> hour
                }

                String.format("%02d:%02d %s", displayHour, minute, amPm)
            } else {
                "08:15 AM"
            }
        } catch (e: Exception) {
            "08:15 AM"
        }
    }
}