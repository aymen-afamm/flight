package com.example.indogo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class TicketActivity : AppCompatActivity() {

    private lateinit var flight: Flight
    private lateinit var pdfGenerator: PdfGenerator

    // Permission request code
    private companion object {
        const val STORAGE_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flight_ticket_layout)

        pdfGenerator = PdfGenerator(this)

        // Get flight data from intent
        flight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("flight", Flight::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("flight")
        } ?: run {
            Toast.makeText(this, "Error loading flight data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        loadFlightData()
    }

    private fun setupUI() {
        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Download button
        findViewById<Button>(R.id.btnDownload).setOnClickListener {
            checkPermissionsAndDownload()
        }
    }

    private fun checkPermissionsAndDownload() {
        // Check if we have storage permission
        if (hasStoragePermission()) {
            // Permission already granted, proceed with download
            downloadTicketAsPdf()
        } else {
            // Request permission
            requestStoragePermission()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, we need READ_MEDIA_IMAGES or we can use MediaStore for downloads
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For older versions, we need WRITE_EXTERNAL_STORAGE
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            this,
            permissions,
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with download
                    downloadTicketAsPdf()
                } else {
                    // Permission denied
                    Toast.makeText(
                        this,
                        "Storage permission is required to download the ticket",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun downloadTicketAsPdf() {
        try {
            // Get all the ticket details
            val passengerName = findViewById<TextView>(R.id.tvPassengerName).text.toString()
            val flightCode = findViewById<TextView>(R.id.tvFlightCode).text.toString()
            val boardingTime = findViewById<TextView>(R.id.tvBoardingTime).text.toString()
            val gate = findViewById<TextView>(R.id.tvGate).text.toString()
            val terminal = findViewById<TextView>(R.id.tvTerminal).text.toString()
            val seatNumber = findViewById<TextView>(R.id.tvSeatNumber).text.toString()

            // Generate PDF
            val pdfFile = pdfGenerator.generateFlightTicketPdf(
                flight = flight,
                passengerName = passengerName,
                flightCode = flightCode,
                boardingTime = boardingTime,
                gate = gate,
                terminal = terminal,
                seatNumber = seatNumber
            )

            // Show success message with file path
            Toast.makeText(
                this,
                "E-Ticket downloaded successfully!\nSaved to: ${pdfFile.absolutePath}",
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

    // ... keep all your existing methods (loadFlightData, updateFlightRoute, etc.) unchanged
    private fun loadFlightData() {
        // Set current date
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())
        findViewById<TextView>(R.id.tvDate).text = currentDate

        // Update flight route
        updateFlightRoute()

        // Update passenger details
        updatePassengerDetails()

        // Update boarding details
        updateBoardingDetails()
    }

    private fun updateFlightRoute() {
        // Find departure layout and update
        val departureLayout = findViewById<android.widget.LinearLayout>(R.id.layoutDeparture)
        (departureLayout.getChildAt(0) as? TextView)?.text = flight.departureCode
        (departureLayout.getChildAt(1) as? TextView)?.text = getAirportName(flight.departureCode)

        // Find flight info layout and update duration
        val flightInfoLayout = findViewById<android.widget.LinearLayout>(R.id.layoutFlightInfo)
        for (i in 0 until flightInfoLayout.childCount) {
            val child = flightInfoLayout.getChildAt(i)
            if (child is TextView && child.text.contains("h")) {
                child.text = flight.duration
                break
            }
        }

        // Find arrival layout (it's the last LinearLayout in the RelativeLayout)
        val relativeLayout = departureLayout.parent as? android.widget.RelativeLayout
        relativeLayout?.let { parent ->
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                if (child is android.widget.LinearLayout &&
                    child.id != R.id.layoutDeparture &&
                    child.id != R.id.layoutFlightInfo) {
                    (child.getChildAt(0) as? TextView)?.text = flight.arrivalCode
                    (child.getChildAt(1) as? TextView)?.text = getAirportName(flight.arrivalCode)
                    break
                }
            }
        }
    }

    private fun updatePassengerDetails() {
        findViewById<TextView>(R.id.tvPassengerName).text = "Shreya Kumar"
        findViewById<TextView>(R.id.tvFlightType).text = "Economy"
        findViewById<TextView>(R.id.tvFlightCode).text = generateFlightCode(flight.airlineName)
    }

    private fun updateBoardingDetails() {
        findViewById<TextView>(R.id.tvBoardingTime).text =
            calculateBoardingTime(flight.departureTime)
        findViewById<TextView>(R.id.tvGate).text = "A5"
        findViewById<TextView>(R.id.tvTerminal).text = "T2"
        findViewById<TextView>(R.id.tvSeatNumber).text = "A5"
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