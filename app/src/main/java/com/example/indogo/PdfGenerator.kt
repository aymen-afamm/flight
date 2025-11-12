package com.example.indogo

import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generateFlightTicketPdf(flight: Flight, passengerName: String, flightCode: String,
                                boardingTime: String, gate: String, terminal: String, seatNumber: String): File {

        // Create file name with timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "FlightTicket_${flight.airlineName.replace(" ", "")}_$timeStamp.pdf"

        // For Android 10+, use app-specific directory or try Downloads
        val file = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Use app-specific directory for Android 10+
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            File(downloadsDir, fileName)
        } else {
            // Use public Downloads directory for older versions
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            File(downloadsDir, fileName)
        }

        // Initialize PDF writer and document
        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Set document properties
        document.setMargins(20f, 20f, 20f, 20f)

        // Add content to PDF
        addHeader(document, flight)
        addFlightDetails(document, flight, passengerName, flightCode, boardingTime, gate, terminal, seatNumber)
        addFooter(document)

        // Close document
        document.close()

        return file
    }

    // ... rest of the PdfGenerator class remains the same
    private fun addHeader(document: Document, flight: Flight) {
        // Airline name
        val airlineText = Text(flight.airlineName)
            .setFontSize(24f)
            .setBold()
            .setFontColor(com.itextpdf.kernel.colors.DeviceRgb(95, 79, 209))

        val airlineParagraph = Paragraph(airlineText)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)

        document.add(airlineParagraph)

        // Current date
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())
        val dateParagraph = Paragraph("Date: $currentDate")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
            .setMarginBottom(30f)

        document.add(dateParagraph)
    }

    private fun addFlightDetails(document: Document, flight: Flight, passengerName: String,
                                 flightCode: String, boardingTime: String, gate: String,
                                 terminal: String, seatNumber: String) {

        // Flight route section
        val routeTable = Table(3)
        routeTable.setWidth(500f)
        routeTable.setHorizontalAlignment(HorizontalAlignment.CENTER)

        // Departure
        val departureCell = Cell()
        departureCell.add(Paragraph(flight.departureCode)
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.LEFT))
        departureCell.add(Paragraph(getAirportName(flight.departureCode))
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.LEFT))

        // Flight icon and duration
        val flightInfoCell = Cell()
        flightInfoCell.add(Paragraph("✈")
            .setFontSize(16f)
            .setTextAlignment(TextAlignment.CENTER))
        flightInfoCell.add(Paragraph(flight.duration)
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER))

        // Arrival
        val arrivalCell = Cell()
        arrivalCell.add(Paragraph(flight.arrivalCode)
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.RIGHT))
        arrivalCell.add(Paragraph(getAirportName(flight.arrivalCode))
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.RIGHT))

        routeTable.addCell(departureCell)
        routeTable.addCell(flightInfoCell)
        routeTable.addCell(arrivalCell)

        document.add(routeTable)
        document.add(Paragraph("\n"))

        // Passenger and meal info
        val infoTable = Table(2)
        infoTable.setWidth(500f)
        infoTable.setHorizontalAlignment(HorizontalAlignment.CENTER)

        infoTable.addCell(Cell().add(Paragraph("👤 01 Adult").setBold()))
        infoTable.addCell(Cell().add(
            Paragraph(if (flight.hasFreeMeal) "🍴 Meal Included" else "🍴 No Meal").setBold()
        ))

        document.add(infoTable)
        document.add(Paragraph("\n"))

        // Passenger details
        val passengerTable = Table(3)
        passengerTable.setWidth(500f)
        passengerTable.setHorizontalAlignment(HorizontalAlignment.CENTER)

        passengerTable.addCell(createDetailCell("Passenger Name", passengerName))
        passengerTable.addCell(createDetailCell("Flight Type", "Economy"))
        passengerTable.addCell(createDetailCell("Flight Code", flightCode))

        document.add(passengerTable)
        document.add(Paragraph("\n"))

        // Boarding details
        val boardingTable = Table(4)
        boardingTable.setWidth(500f)
        boardingTable.setHorizontalAlignment(HorizontalAlignment.CENTER)

        boardingTable.addCell(createDetailCell("Boarding Time", boardingTime))
        boardingTable.addCell(createDetailCell("Gate", gate))
        boardingTable.addCell(createDetailCell("Terminal", terminal))
        boardingTable.addCell(createDetailCell("Seat Number", seatNumber))

        document.add(boardingTable)
        document.add(Paragraph("\n"))

        // Price
        val priceParagraph = Paragraph("Total Paid: ₹${flight.price}")
            .setFontSize(16f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20f)

        document.add(priceParagraph)

        // Promo code if available
        if (flight.promoCode.isNotEmpty()) {
            val promoParagraph = Paragraph(flight.promoCode)
                .setFontSize(10f)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10f)

            document.add(promoParagraph)
        }
    }

    private fun addFooter(document: Document) {
        document.add(Paragraph("\n\n"))

        val termsHeader = Paragraph("Terms & Conditions")
            .setBold()
            .setFontSize(12f)
            .setMarginBottom(5f)

        val termsText = Paragraph("This is your electronic ticket. Please present this document along with a valid government-issued photo ID at the airport security check and boarding gate. Boarding begins 45 minutes before departure and gates close 20 minutes before departure.")
            .setFontSize(10f)
            .setTextAlignment(TextAlignment.JUSTIFIED)

        document.add(termsHeader)
        document.add(termsText)
    }

    private fun createDetailCell(label: String, value: String): Cell {
        val cell = Cell()
        cell.add(Paragraph(label)
            .setFontSize(9f)
            .setFontColor(ColorConstants.GRAY))
        cell.add(Paragraph(value)
            .setFontSize(12f)
            .setBold())
        return cell
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
}