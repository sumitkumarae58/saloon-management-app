package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity
import com.example.ui.SalonViewModel
import com.example.ui.theme.*

@Composable
fun CustomerScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSalon by viewModel.selectedSalon.collectAsState()
    val bookingSuccess by viewModel.bookingSuccess.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(PureWhite)) {
        AnimatedContent(
            targetState = selectedSalon,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ScreenTransition"
        ) { salon ->
            if (salon == null) {
                // Landing Marketplace Screen
                MarketplaceLanding(viewModel)
            } else {
                if (bookingSuccess) {
                    // Success View
                    BookingSuccessScreen(viewModel, salon)
                } else {
                    // Salon Detail + Booking Flow
                    SalonDetailScreen(viewModel, salon)
                }
            }
        }
    }
}

@Composable
fun MarketplaceLanding(viewModel: SalonViewModel) {
    val salons by viewModel.allSalons.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val customerEmail by viewModel.customerEmailInput.collectAsState()
    val bookings by viewModel.customerBookings.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()

    var showDashboard by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT LOCATION",
                            color = SlateMedium.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mayfair, London",
                                color = SlateDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Dashboard toggle or customer profile button
                    Button(
                        onClick = { showDashboard = !showDashboard },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showDashboard) IndigoPrimary else SlateDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("dashboard_toggle")
                    ) {
                        Icon(
                            imageVector = if (showDashboard) Icons.Default.Search else Icons.Default.CalendarMonth,
                            contentDescription = "My Appointments",
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showDashboard) "Explore" else "My Bookings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!showDashboard) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search services, styles, or salons...", color = SlateMedium.copy(alpha = 0.4f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SlateMedium.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("salon_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary.copy(alpha = 0.3f),
                            unfocusedBorderColor = SlateBorder,
                            focusedContainerColor = SlateLight,
                            unfocusedContainerColor = SlateLight
                        ),
                        singleLine = true
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showDashboard) {
            CustomerDashboardView(
                viewModel = viewModel,
                customerEmail = customerEmail,
                bookings = bookings,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            // Filtered Salons list
            val filteredSalons = salons.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.address.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(PureWhite),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Featured Salon Card (when search query is empty and we have salons)
                if (searchQuery.isEmpty() && salons.isNotEmpty()) {
                    item {
                        val featured = salons.first()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Featured Salon",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Text(
                                    text = "View All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary,
                                    modifier = Modifier.clickable {
                                        viewModel.selectSalon(featured)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                                    .clickable { viewModel.selectSalon(featured) },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateDark),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Image background
                                    Image(
                                        painter = painterResource(id = R.drawable.img_salon_hero),
                                        contentDescription = featured.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Gradient Overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                                    startY = 150f
                                                )
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(18.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(EmeraldConfirmed, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "CONFIRMED TOP-RATED",
                                                color = PureWhite,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = featured.name,
                                            color = PureWhite,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Rating",
                                                    tint = AmberPending,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = featured.rating.toString(),
                                                    color = PureWhite,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = "•", color = PureWhite.copy(alpha = 0.5f))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "0.8 miles away",
                                                color = PureWhite.copy(alpha = 0.9f),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Popular Services Row (Categories filter list)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Popular Services",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val categories = listOf(
                            Triple("Haircut", Icons.Default.ContentCut, "Hair"),
                            Triple("Beard", Icons.Default.Brush, "Beard"),
                            Triple("Spa/Combo", Icons.Default.Spa, "Combo"),
                            Triple("Facial", Icons.Default.Face, "Facial")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            categories.forEach { (label, icon, query) ->
                                val isSelected = searchQuery.equals(query, ignoreCase = true)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (isSelected) {
                                                viewModel.updateSearchQuery("")
                                            } else {
                                                viewModel.updateSearchQuery(query)
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .background(
                                                if (isSelected) IndigoLight else PureWhite,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) IndigoPrimary else SlateBorder,
                                                RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSelected) IndigoPrimary else SlateDark,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) IndigoPrimary else SlateMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Expert Stylists Horizontal Roster
                if (searchQuery.isEmpty() && staffList.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Expert Stylists",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            androidx.compose.foundation.lazy.LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(staffList) { staff ->
                                    Card(
                                        modifier = Modifier
                                            .width(180.dp)
                                            .clickable {
                                                // Book directly or view salon
                                                val associatedSalon = salons.firstOrNull { it.id == staff.salonId }
                                                if (associatedSalon != null) {
                                                    viewModel.selectSalon(associatedSalon)
                                                }
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                                        border = BorderStroke(1.dp, SlateBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(40.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(IndigoLight, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    val initials = staff.name.split(" ")
                                                        .mapNotNull { it.firstOrNull() }
                                                        .take(2)
                                                        .joinToString("")
                                                    Text(
                                                        text = initials,
                                                        color = IndigoPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                                // Online Green dot indicator
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .background(EmeraldConfirmed, CircleShape)
                                                        .border(1.5.dp, PureWhite, CircleShape)
                                                        .align(Alignment.BottomEnd)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = staff.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = SlateDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = staff.role,
                                                    fontSize = 9.sp,
                                                    color = SlateMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Explore Salons List (Header)
                item {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Search Results" else "Explore Salons",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // 5. Show matching/filtered salons
                val displayedSalons = if (searchQuery.isEmpty() && filteredSalons.size > 1) {
                    filteredSalons.drop(1) // Avoid duplicate featured salon
                } else {
                    filteredSalons
                }

                if (displayedSalons.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No results",
                                    tint = SlateBorder,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Salons Found",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMedium
                                )
                            }
                        }
                    }
                } else {
                    items(displayedSalons) { salon ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            SalonCard(salon = salon, onClick = { viewModel.selectSalon(salon) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SalonCard(salon: SalonEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("salon_card_${salon.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Background image
                val imageRes = if (salon.imageResName == "img_salon_hero") {
                    R.drawable.img_salon_hero
                } else {
                    R.drawable.img_barber_tools
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark overlay at the bottom for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 250f
                            )
                        )
                )

                // Rating Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(PureWhite, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AmberPending,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = salon.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SlateDark
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = salon.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = SlateMedium,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = salon.address,
                        fontSize = 12.sp,
                        color = SlateMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = salon.description,
                    fontSize = 13.sp,
                    color = SlateMedium.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hours: ${salon.openingHours}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateMedium
                    )

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("View Services", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SalonDetailScreen(viewModel: SalonViewModel, salon: SalonEntity) {
    val services by viewModel.allServices.collectAsState()
    val staffList by viewModel.allStaff.collectAsState()
    val bookingStep by viewModel.bookingStep.collectAsState()

    var showBookingSheet by remember { mutableStateOf(false) }

    val salonServices = services.filter { it.salonId == salon.id }
    val salonStaff = staffList.filter { it.salonId == salon.id }

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + salonServices.map { it.category }.distinct()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val imageRes = if (salon.imageResName == "img_salon_hero") {
                    R.drawable.img_salon_hero
                } else {
                    R.drawable.img_barber_tools
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = salon.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.selectSalon(null) },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Rating badge
                        Row(
                            modifier = Modifier
                                .background(PureWhite, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(salon.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SlateDark)
                        }
                    }

                    Column {
                        Text(
                            text = salon.name,
                            color = PureWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Address", tint = SlateBorder, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(salon.address, color = SlateBorder, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // About
                item {
                    Column {
                        Text("About Us", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = salon.description,
                            fontSize = 14.sp,
                            color = SlateMedium.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, "Phone", tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(salon.phone, fontSize = 13.sp, color = SlateDark, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(20.dp))
                            Icon(Icons.Default.AccessTime, "Hours", tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(salon.openingHours, fontSize = 13.sp, color = SlateDark, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Stylists
                item {
                    Column {
                        Text("Our Expert Stylists", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (staff in salonStaff) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SlateLight),
                                    border = BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(IndigoPrimary.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = staff.name.split(" ").firstOrNull()?.take(1) ?: "S",
                                                color = IndigoPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = staff.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = SlateDark,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = staff.role,
                                            fontSize = 10.sp,
                                            color = SlateMedium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(staff.rating.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Services Catalog
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Services Catalog", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)

                            // Quick Categories
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                categories.take(3).forEach { cat ->
                                    FilterChip(
                                        selected = selectedCategory == cat,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IndigoPrimary,
                                            selectedLabelColor = PureWhite
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val filteredServices = if (selectedCategory == "All") salonServices else salonServices.filter { it.category == selectedCategory }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (service in filteredServices) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                                    border = BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = service.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = SlateDark
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccessTime, "Duration", tint = SlateMedium, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${service.durationMinutes} mins", fontSize = 12.sp, color = SlateMedium)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = service.description,
                                                fontSize = 11.sp,
                                                color = SlateMedium.copy(alpha = 0.7f),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "$${String.format("%.0f", service.price)}",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = IndigoPrimary
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.selectBookingService(service)
                                                    showBookingSheet = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SlateDark),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(32.dp).testTag("book_service_${service.id}")
                                            ) {
                                                Text("Book", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Booking Modal Trigger Screen Sheet Overlay
            if (showBookingSheet) {
                BookingWizardModal(
                    viewModel = viewModel,
                    salonStaff = salonStaff,
                    onDismiss = {
                        showBookingSheet = false
                        viewModel.resetBookingWizard()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingWizardModal(
    viewModel: SalonViewModel,
    salonStaff: List<StaffEntity>,
    onDismiss: () -> Unit
) {
    val bookingStep by viewModel.bookingStep.collectAsState()
    val bookingService by viewModel.bookingService.collectAsState()
    val bookingStaff by viewModel.bookingStaff.collectAsState()
    val bookingDate by viewModel.bookingDate.collectAsState()
    val bookingTime by viewModel.bookingTime.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PureWhite,
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Book Appointment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    Text(
                        text = bookingService?.name ?: "Select Service",
                        fontSize = 13.sp,
                        color = SlateMedium
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = RoseCancelled, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Indicator Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..4) {
                    val isActive = i <= bookingStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isActive) IndigoPrimary else SlateBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (bookingStep) {
                    1 -> {
                        // Normally service is selected, fallback
                        Text("Please select service first.")
                    }
                    2 -> {
                        // Select Barber
                        SelectBarberStep(
                            salonStaff = salonStaff,
                            selectedStaff = bookingStaff,
                            onStaffSelect = { viewModel.selectBookingStaff(it) }
                        )
                    }
                    3 -> {
                        // Select Date & Time
                        SelectDateTimeStep(
                            selectedDate = bookingDate,
                            selectedTime = bookingTime,
                            onSelect = { date, time -> viewModel.selectBookingDateTime(date, time) }
                        )
                    }
                    4 -> {
                        // Contact Details
                        ContactDetailsStep(
                            service = bookingService,
                            staff = bookingStaff,
                            date = bookingDate,
                            time = bookingTime,
                            onSubmit = { name, email, phone, notes ->
                                viewModel.submitBooking(name, email, phone, notes)
                            }
                        )
                    }
                }
            }

            // Bottom Nav Buttons
            if (bookingStep > 2 && bookingStep < 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevBookingStep() },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, SlateBorder)
                    ) {
                        Text("Back", color = SlateDark)
                    }

                    Button(
                        onClick = { viewModel.nextBookingStep() },
                        enabled = (bookingStep == 3 && bookingTime != null),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }
}

@Composable
fun SelectBarberStep(
    salonStaff: List<StaffEntity>,
    selectedStaff: StaffEntity?,
    onStaffSelect: (StaffEntity?) -> Unit
) {
    Column {
        Text(
            text = "Select a Stylist",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Text(
            text = "Choose your professional or select Any Available to skip preferences.",
            fontSize = 12.sp,
            color = SlateMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Any Available Special Barber Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStaffSelect(null) }
                        .testTag("barber_any"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedStaff == null) IndigoLight else PureWhite
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedStaff == null) IndigoPrimary else SlateBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(IndigoPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, "Any", tint = IndigoPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Any Available Stylist", fontWeight = FontWeight.Bold, color = SlateDark, fontSize = 14.sp)
                            Text("Select this for instant scheduling flexibility.", fontSize = 12.sp, color = SlateMedium)
                        }
                        if (selectedStaff == null) {
                            Icon(Icons.Default.CheckCircle, "Selected", tint = IndigoPrimary)
                        }
                    }
                }
            }

            items(salonStaff) { staff ->
                val isSelected = selectedStaff?.id == staff.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStaffSelect(staff) }
                        .testTag("barber_${staff.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) IndigoLight else PureWhite
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) IndigoPrimary else SlateBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(SlateBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = staff.name.take(1),
                                color = SlateDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(staff.name, fontWeight = FontWeight.Bold, color = SlateDark, fontSize = 14.sp)
                            Text(staff.role, fontSize = 11.sp, color = SlateMedium)
                            Text(
                                text = staff.bio,
                                fontSize = 10.sp,
                                color = SlateMedium.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, "Rating", tint = AmberPending, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(staff.rating.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            }
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(Icons.Default.CheckCircle, "Selected", tint = IndigoPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectDateTimeStep(
    selectedDate: String,
    selectedTime: String?,
    onSelect: (String, String) -> Unit
) {
    // Dates listing for upcoming 6 days from Jun 28
    val dates = listOf(
        Pair("Jun 28", "2026-06-28"),
        Pair("Jun 29", "2026-06-29"),
        Pair("Jun 30", "2026-06-30"),
        Pair("Jul 01", "2026-07-01"),
        Pair("Jul 02", "2026-07-02"),
        Pair("Jul 03", "2026-07-03")
    )

    val timeSlots = listOf(
        "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
        "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM",
        "05:00 PM", "06:00 PM"
    )

    var activeDate by remember { mutableStateOf(selectedDate) }

    Column {
        Text(
            text = "Choose Date & Time",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Date selection row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            dates.forEach { (label, value) ->
                val isSelected = activeDate == value
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeDate = value },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SlateDark else SlateLight
                    ),
                    border = BorderStroke(1.dp, if (isSelected) SlateDark else SlateBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) PureWhite else SlateDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Available Slots",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(timeSlots) { slot ->
                val isSelected = selectedTime == slot && selectedDate == activeDate
                Card(
                    modifier = Modifier
                        .clickable { onSelect(activeDate, slot) }
                        .testTag("time_slot_$slot"),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) IndigoPrimary else PureWhite
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) IndigoPrimary else SlateBorder
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PureWhite else SlateDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactDetailsStep(
    service: ServiceEntity?,
    staff: StaffEntity?,
    date: String,
    time: String?,
    onSubmit: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Column {
                Text(
                    text = "Confirm Booking details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Summary Box
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateLight),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(service?.name ?: "", fontWeight = FontWeight.Bold, color = SlateDark)
                            Text("$${String.format("%.2f", service?.price ?: 0.0)}", fontWeight = FontWeight.ExtraBold, color = IndigoPrimary)
                        }
                        Text("Stylist: ${staff?.name ?: "Any Available"}", fontSize = 12.sp, color = SlateMedium)
                        Text("Date & Time: $date at ${time ?: "10:00 AM"}", fontSize = 12.sp, color = SlateMedium)
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Outlined.Person, "Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Outlined.Email, "Email") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_email"),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Outlined.Phone, "Phone") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_phone"),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Requests / Hair type notes") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_customer_notes"),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMsg != null) {
                    Text(errorMsg!!, color = RoseCancelled, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
                            errorMsg = "Please fill in all contact fields."
                        } else {
                            errorMsg = null
                            onSubmit(name, email, phone, notes)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_booking_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm Appointment", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BookingSuccessScreen(viewModel: SalonViewModel, salon: SalonEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(EmeraldConfirmed.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = EmeraldConfirmed,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Booking Confirmed!",
            color = PureWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your appointment has been successfully scheduled at ${salon.name}.",
            color = SlateLight.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateMedium)
        ) {
            val bookingService by viewModel.bookingService.collectAsState()
            val bookingStaff by viewModel.bookingStaff.collectAsState()
            val bookingDate by viewModel.bookingDate.collectAsState()
            val bookingTime by viewModel.bookingTime.collectAsState()

            Column(modifier = Modifier.padding(20.dp)) {
                Text("APPOINTMENT DETAILS", color = SlateBorder, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Service", color = PureWhite, fontWeight = FontWeight.Bold)
                    Text(bookingService?.name ?: "", color = PureWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Stylist", color = PureWhite, fontWeight = FontWeight.Bold)
                    Text(bookingStaff?.name ?: "Any Available", color = PureWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Date", color = PureWhite, fontWeight = FontWeight.Bold)
                    Text(bookingDate, color = PureWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Time", color = PureWhite, fontWeight = FontWeight.Bold)
                    Text(bookingTime ?: "", color = PureWhite)
                }

                Divider(color = SlateDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amount Paid (at venue)", color = SlateBorder, fontSize = 12.sp)
                    Text("$${String.format("%.2f", bookingService?.price ?: 0.0)}", color = EmeraldConfirmed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                viewModel.selectSalon(null)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Back to Explore", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomerDashboardView(
    viewModel: SalonViewModel,
    customerEmail: String,
    bookings: List<BookingEntity>,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf(customerEmail) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .padding(20.dp)
    ) {
        Text(
            text = "My Appointments",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Text(
            text = "View, manage, or reschedule your upcoming wellness services.",
            fontSize = 12.sp,
            color = SlateMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email Quick "Login" to filter appointments
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                placeholder = { Text("Enter your email to load bookings...", color = SlateMedium.copy(alpha = 0.4f)) },
                modifier = Modifier.weight(1f).testTag("customer_email_filter"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary.copy(alpha = 0.3f),
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = SlateLight,
                    unfocusedContainerColor = SlateLight
                ),
                singleLine = true
            )

            Button(
                onClick = { viewModel.updateCustomerEmail(emailInput) },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "No appointments",
                        tint = SlateBorder,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Appointments Found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium
                    )
                    Text(
                        text = "Book a new service to see your schedule here.",
                        fontSize = 12.sp,
                        color = SlateMedium.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings) { booking ->
                    BookingItemCard(booking = booking, onCancel = { viewModel.cancelBooking(booking) })
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(booking: BookingEntity, onCancel: () -> Unit) {
    val statusColor = when (booking.status) {
        "Confirmed" -> EmeraldConfirmed
        "Completed" -> BlueCompleted
        "Cancelled" -> RoseCancelled
        else -> AmberPending // Pending
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
                    Text(
                        text = booking.salonName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = SlateDark
                    )
                    Text(
                        text = booking.serviceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = IndigoPrimary
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Date: ${booking.date}", fontSize = 11.sp, color = SlateMedium)
                    Text("Time: ${booking.timeSlot}", fontSize = 11.sp, color = SlateMedium)
                    Text("Stylist: ${booking.staffName}", fontSize = 11.sp, color = SlateMedium)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", booking.servicePrice)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = SlateDark,
                        fontSize = 14.sp
                    )
                    if (booking.status == "Pending" || booking.status == "Confirmed") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cancel Appointment",
                            color = RoseCancelled,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onCancel() }
                                .padding(vertical = 2.dp)
                                .testTag("cancel_booking_${booking.id}")
                        )
                    }
                }
            }
        }
    }
}
