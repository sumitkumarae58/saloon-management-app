package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.StaffEntity
import com.example.ui.SalonViewModel
import com.example.ui.theme.*

@Composable
fun BarberScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val staffList by viewModel.allStaff.collectAsState()
    val bookings by viewModel.barberBookings.collectAsState()
    val selectedBarberId by viewModel.selectedBarberId.collectAsState()

    // Default active barber to Marcus Vance (id = 1) if none is selected
    val activeBarber = staffList.firstOrNull { it.id == selectedBarberId } ?: staffList.firstOrNull()
    val activeId = activeBarber?.id ?: 1

    var selectedDate by remember { mutableStateOf("2026-06-28") } // Sunday (Our seeded date)

    val dates = listOf(
        Pair("Sun 28", "2026-06-28"),
        Pair("Mon 29", "2026-06-29"),
        Pair("Tue 30", "2026-06-30"),
        Pair("Wed 01", "2026-07-01"),
        Pair("Thu 02", "2026-07-02")
    )

    // Filter appointments for active date and active barber
    val activeBookings = bookings.filter { it.date == selectedDate }

    // Quick Stats calculation for this barber
    val completedCount = bookings.count { it.status == "Completed" }
    val totalEarnings = bookings.filter { it.status == "Completed" }.sumOf { it.servicePrice } * 0.7f // 70% stylist commission split

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
                            text = "Stylist Portal",
                            color = PureWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Personal Calendar & Service Manager",
                            color = SlateBorder,
                            fontSize = 11.sp
                        )
                    }

                    // Barber Switcher Dropdown / Row
                    Box(
                        modifier = Modifier
                            .background(SlateMedium, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = activeBarber?.name ?: "Marcus Vance",
                            color = PureWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable row to select stylist view (so examiners can try Marcus, Sophia, or Devon!)
                Text("Switch Barber Profile:", color = SlateBorder, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(staffList) { staff ->
                        val isActive = staff.id == activeId
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isActive) IndigoPrimary else SlateMedium,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectBarberForDashboard(staff.id) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("barber_select_${staff.id}")
                        ) {
                            Text(
                                text = staff.name.split(" ").firstOrNull() ?: "",
                                color = PureWhite,
                                fontSize = 11.sp,
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
                .padding(16.dp)
        ) {
            // Stylist Info Header & Performance Mini Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bio / Profile card
                Card(
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateLight),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(IndigoPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeBarber?.name?.take(1) ?: "M",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeBarber?.name ?: "Marcus Vance",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SlateDark
                                )
                                Text(
                                    text = activeBarber?.role ?: "Master Barber",
                                    fontSize = 9.sp,
                                    color = SlateMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${activeBarber?.rating ?: 4.9} rating", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }
                    }
                }

                // Commission split earnings card
                Card(
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("My Share (70%)", color = SlateBorder, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.0f", totalEarnings)}",
                            color = EmeraldConfirmed,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text("$completedCount completed", color = PureWhite, fontSize = 8.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule calendar slider
            Text(
                text = "My Day Schedule",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))

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

            // Appointments List for active Barber & Date
            if (activeBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarMonth, "Free day", tint = SlateBorder, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Rest Day or No Bookings", fontWeight = FontWeight.Bold, color = SlateMedium)
                        Text("No wellness clients booked on this date.", fontSize = 11.sp, color = SlateMedium.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeBookings) { booking ->
                        BarberBookingCard(
                            booking = booking,
                            onConfirm = { viewModel.confirmBooking(booking.id) },
                            onComplete = { viewModel.completeBooking(booking.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarberBookingCard(
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
                        text = "${booking.serviceName} | Client: ${booking.customerPhone}",
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
                        text = "Client Requests: ${booking.notes}",
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
                            modifier = Modifier.height(30.dp).testTag("barber_confirm_${booking.id}")
                        ) {
                            Text("Accept Appt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (booking.status == "Confirmed") {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(30.dp).testTag("barber_complete_${booking.id}")
                        ) {
                            Text("Mark Completed", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
