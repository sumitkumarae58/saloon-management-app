package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity
import com.example.ui.SalonViewModel
import com.example.ui.theme.*

@Composable
fun OwnerScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val salons by viewModel.allSalons.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()

    var activeTab by remember { mutableStateOf("Calendar") } // "Calendar", "Services", "Staff", "Customers", "Analytics"

    val primarySalon = salons.firstOrNull() // Default to primary salon
    val salonId = primarySalon?.id ?: 1

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateDark)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Luxe Business Manager",
                            color = PureWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = primarySalon?.name ?: "Salon Luxe",
                            color = SlateBorder,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(IndigoPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Owner Dashboard", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable tab row for navigation
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf("Calendar", "Services", "Staff", "Customers", "Analytics")
                    items(tabs) { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) IndigoPrimary else SlateMedium,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { activeTab = tab }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("owner_tab_$tab")
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) PureWhite else SlateLight.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureWhite)
        ) {
            when (activeTab) {
                "Calendar" -> OwnerCalendarTab(viewModel, bookings)
                "Services" -> OwnerServicesTab(viewModel, services, salonId)
                "Staff" -> OwnerStaffTab(viewModel, staffList, salonId)
                "Customers" -> OwnerCustomersTab(bookings)
                "Analytics" -> OwnerAnalyticsTab(bookings, staffList)
            }
        }
    }
}

// --- CALENDAR SCHEDULE TAB ---
@Composable
fun OwnerCalendarTab(viewModel: SalonViewModel, bookings: List<BookingEntity>) {
    var selectedDate by remember { mutableStateOf("2026-06-28") } // Sunday (Our seeded date)

    val dates = listOf(
        Pair("Sun 28", "2026-06-28"),
        Pair("Mon 29", "2026-06-29"),
        Pair("Tue 30", "2026-06-30"),
        Pair("Wed 01", "2026-07-01"),
        Pair("Thu 02", "2026-07-02")
    )

    val filteredBookings = bookings.filter { it.date == selectedDate }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Shop Schedule Overview",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Text(
            text = "Track your stylists' timetables, adjust bookings, or accept pending requests.",
            fontSize = 11.sp,
            color = SlateMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Date selector row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            dates.forEach { (label, value) ->
                val isSelected = selectedDate == value
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDate = value },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SlateDark else SlateLight
                    ),
                    border = BorderStroke(1.dp, if (isSelected) SlateDark else SlateBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label.split(" ").firstOrNull() ?: "",
                            fontSize = 10.sp,
                            color = if (isSelected) SlateLight else SlateMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = label.split(" ").lastOrNull() ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) PureWhite else SlateDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CalendarToday, "Empty", tint = SlateBorder, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Appointments Today", fontWeight = FontWeight.Bold, color = SlateMedium)
                    Text("There are no bookings scheduled on this date.", fontSize = 12.sp, color = SlateMedium.copy(alpha = 0.7f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredBookings) { booking ->
                    OwnerBookingScheduleCard(booking = booking, onConfirm = { viewModel.confirmBooking(booking.id) }, onComplete = { viewModel.completeBooking(booking.id) })
                }
            }
        }
    }
}

@Composable
fun OwnerBookingScheduleCard(
    booking: BookingEntity,
    onConfirm: () -> Unit,
    onComplete: () -> Unit
) {
    val statusColor = when (booking.status) {
        "Confirmed" -> EmeraldConfirmed
        "Completed" -> BlueCompleted
        "Cancelled" -> RoseCancelled
        else -> AmberPending
    }

    val statusBg = when (booking.status) {
        "Confirmed" -> EmeraldLight
        "Completed" -> BlueLight
        "Cancelled" -> RoseLight
        else -> AmberLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, SlateBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(IndigoPrimary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(booking.timeSlot, color = IndigoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(booking.customerName, fontWeight = FontWeight.Bold, color = SlateDark, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${booking.serviceName} with ${booking.staffName}",
                        fontSize = 12.sp,
                        color = SlateMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(booking.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (booking.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateLight, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Notes: ${booking.notes}",
                        fontSize = 11.sp,
                        color = SlateMedium,
                        lineHeight = 14.sp
                    )
                }
            }

            // Quick Actions based on status
            if (booking.status == "Pending" || booking.status == "Confirmed") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (booking.status == "Pending") {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldConfirmed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp).testTag("schedule_confirm_${booking.id}")
                        ) {
                            Icon(Icons.Default.Check, "Confirm", tint = PureWhite, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirm", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (booking.status == "Confirmed") {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp).testTag("schedule_complete_${booking.id}")
                        ) {
                            Icon(Icons.Default.DoneAll, "Complete", tint = PureWhite, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete Service", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SERVICES TAB ---
@Composable
fun OwnerServicesTab(
    viewModel: SalonViewModel,
    services: List<ServiceEntity>,
    salonId: Int
) {
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog Input states
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Hair") }
    var desc by remember { mutableStateOf("") }

    val salonServices = services.filter { it.salonId == salonId }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Services Catalog", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    Text("Manage prices, durations, and categories.", fontSize = 11.sp, color = SlateMedium)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_service_button")
                ) {
                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Service", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(salonServices) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(service.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(SlateLight, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(service.category, color = SlateMedium, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${service.durationMinutes} mins | ${service.description}", fontSize = 11.sp, color = SlateMedium)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$${String.format("%.2f", service.price)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndigoPrimary,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                IconButton(onClick = { viewModel.removeService(service) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = RoseCancelled, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            val priceVal = price.toDoubleOrNull() ?: 0.0
                            val durVal = duration.toIntOrNull() ?: 30
                            if (name.isNotBlank()) {
                                viewModel.addNewService(salonId, name, priceVal, durVal, category, desc)
                                showAddDialog = false
                                // reset inputs
                                name = ""
                                price = ""
                                duration = ""
                                desc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = SlateDark)
                    }
                },
                title = { Text("Add New Service") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SlateDark,
                            unfocusedTextColor = SlateDark,
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedLabelColor = SlateDark,
                            unfocusedLabelColor = SlateMedium,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Service Name") },
                            modifier = Modifier.fillMaxWidth().testTag("add_service_name"),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price ($)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_service_price"),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration (mins)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_service_duration"),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )

                        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Hair", "Beard", "Facial", "Combo").forEach { cat ->
                                val isSelected = category == cat
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { category = cat },
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) IndigoPrimary else SlateLight),
                                    border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SlateBorder)
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text(cat, color = if (isSelected) PureWhite else SlateDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = PureWhite
            )
        }
    }
}

// --- STAFF DIRECTORY TAB ---
@Composable
fun OwnerStaffTab(
    viewModel: SalonViewModel,
    staffList: List<StaffEntity>,
    salonId: Int
) {
    var showAddDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Master Barber") }
    var bio by remember { mutableStateOf("") }

    val salonStaff = staffList.filter { it.salonId == salonId }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Staff Directory", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    Text("Manage stylist roster, ratings, and roles.", fontSize = 11.sp, color = SlateMedium)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_staff_button")
                ) {
                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Barber", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(salonStaff) { staff ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(IndigoPrimary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(staff.name.take(1), color = IndigoPrimary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                    Text(staff.role, fontSize = 11.sp, color = SlateMedium)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(staff.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark, modifier = Modifier.padding(end = 12.dp))

                                IconButton(onClick = { viewModel.removeStaff(staff) }) {
                                    Icon(Icons.Default.Delete, "Delete", tint = RoseCancelled, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.addNewStaff(salonId, name, role, 4.8, bio)
                                showAddDialog = false
                                name = ""
                                bio = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = SlateDark)
                    }
                },
                title = { Text("Add Stylist / Barber") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SlateDark,
                            unfocusedTextColor = SlateDark,
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = SlateBorder,
                            focusedLabelColor = SlateDark,
                            unfocusedLabelColor = SlateMedium,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Barber Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("add_staff_name"),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Role Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Brief Bio") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                    }
                },
                containerColor = PureWhite
            )
        }
    }
}

// --- CUSTOMERS TAB ---
@Composable
fun OwnerCustomersTab(bookings: List<BookingEntity>) {
    // Group completed bookings by customer email to make a dynamic, beautiful list of customers!
    val customerSummary = bookings
        .groupBy { it.customerEmail }
        .map { (email, customerBookings) ->
            val firstBooking = customerBookings.first()
            val totalSpent = customerBookings.filter { it.status == "Completed" || it.status == "Confirmed" }.sumOf { it.servicePrice }
            val completedCount = customerBookings.count { it.status == "Completed" }
            Triple(firstBooking.customerName, email, Pair(completedCount, totalSpent))
        }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Active Customer Roster", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Text("SaaS-style customer database tracking total services and spend.", fontSize = 11.sp, color = SlateMedium, modifier = Modifier.padding(bottom = 12.dp))

        if (customerSummary.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("No customers registered yet.", color = SlateMedium)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items(customerSummary) { (name, email, stats) ->
                    val (completed, spent) = stats
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                Text(email, fontSize = 11.sp, color = SlateMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Box(modifier = Modifier.background(IndigoLight, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("$completed visits", color = IndigoPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Spent", fontSize = 10.sp, color = SlateMedium)
                                Text("$${String.format("%.2f", spent)}", fontWeight = FontWeight.ExtraBold, color = EmeraldConfirmed, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ANALYTICS TAB ---
@Composable
fun OwnerAnalyticsTab(bookings: List<BookingEntity>, staffList: List<StaffEntity>) {
    val totalRevenue = bookings.filter { it.status == "Completed" || it.status == "Confirmed" }.sumOf { it.servicePrice }
    val newCustomers = bookings.map { it.customerEmail }.distinct().size
    val totalBookings = bookings.size
    val completionRate = if (totalBookings > 0) (bookings.count { it.status == "Completed" }.toFloat() / totalBookings * 100).toInt() else 0

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Business Analytics Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Spacer(modifier = Modifier.height(14.dp))

        // Three clean SaaS Metrics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDark)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Revenue", color = SlateBorder, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$${String.format("%.0f", totalRevenue)}", color = PureWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("+12.5% vs last wk", color = EmeraldConfirmed, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateLight),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Customers Registered", color = SlateMedium, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$newCustomers", color = SlateDark, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("+4 new today", color = IndigoPrimary, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateLight),
                border = BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Occupancy / Load", color = SlateMedium, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$completionRate%", color = SlateDark, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Max load Saturday", color = SlateMedium, fontSize = 8.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Beautiful, Custom Canvas-based Revenue Trend Line Chart with Slate and Indigo accents!
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = BorderStroke(1.dp, SlateBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weekly Revenue Trend", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateDark)
                Spacer(modifier = Modifier.height(6.dp))

                // Line Chart drawn via Jetpack Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(top = 10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Graph points
                        val points = listOf(150f, 320f, 240f, 480f, 380f, 550f, 620f)
                        val maxVal = points.maxOrNull() ?: 1000f
                        val width = size.width
                        val height = size.height

                        val path = Path()
                        val stepX = width / (points.size - 1)

                        points.forEachIndexed { idx, point ->
                            val x = idx * stepX
                            val y = height - (point / maxVal) * (height * 0.8f) - 10f

                            if (idx == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        // Draw smooth gradient stroke line
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(listOf(IndigoPrimary, EmeraldConfirmed)),
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                // X-axis Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { label ->
                        Text(label, fontSize = 10.sp, color = SlateMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
