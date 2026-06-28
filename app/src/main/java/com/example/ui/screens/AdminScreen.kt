package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SalonEntity
import com.example.ui.SalonViewModel
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val salons by viewModel.allSalons.collectAsState()
    val bookings by viewModel.allBookings.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Form inputs
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("09:00 AM - 08:00 PM") }
    var description by remember { mutableStateOf("") }

    val platformVolume = bookings.filter { it.status == "Completed" || it.status == "Confirmed" }.sumOf { it.servicePrice }
    val averageBookingCost = if (bookings.isNotEmpty()) platformVolume / bookings.size else 0.0

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
                            text = "Platform Super Admin",
                            color = PureWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Global Salon Network Hub",
                            color = SlateBorder,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(RoseCancelled, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Root Admin", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PureWhite)
                    .padding(16.dp)
            ) {
                // Platform KPI Metrics row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateDark)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Platform GMV Volume", color = SlateBorder, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$${String.format("%.0f", platformVolume)}", color = PureWhite, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateLight),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Active Salons", color = SlateMedium, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${salons.size} storefronts", color = SlateDark, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateLight),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Average Ticket", color = SlateMedium, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$${String.format("%.0f", averageBookingCost)}", color = IndigoPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Global Shop Registrations",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp).testTag("admin_register_shop_button")
                    ) {
                        Icon(Icons.Default.Add, "Register", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Register Salon", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(salons) { salon ->
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
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(IndigoPrimary.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Business, "Salon", tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(salon.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                                        Text(salon.address, fontSize = 11.sp, color = SlateMedium)
                                        Text("Hours: ${salon.openingHours}", fontSize = 10.sp, color = SlateMedium.copy(alpha = 0.8f))
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(salon.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark, modifier = Modifier.padding(end = 8.dp))
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
                                if (name.isNotBlank() && address.isNotBlank()) {
                                    viewModel.addNewSalon(
                                        name = name,
                                        address = address,
                                        phone = phone,
                                        hours = hours,
                                        description = description
                                    )
                                    showAddDialog = false
                                    // Clear
                                    name = ""
                                    address = ""
                                    phone = ""
                                    description = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel", color = SlateDark)
                        }
                    },
                    title = { Text("Register New Salon Store") },
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
                                label = { Text("Salon Name") },
                                modifier = Modifier.fillMaxWidth().testTag("add_salon_name"),
                                colors = textFieldColors
                            )
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Street Address") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Contact Phone") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                            OutlinedTextField(
                                value = hours,
                                onValueChange = { hours = it },
                                label = { Text("Opening Hours") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Storefront Description") },
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
}
