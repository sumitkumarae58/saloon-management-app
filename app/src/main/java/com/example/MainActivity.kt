package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SalonViewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BarberScreen
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.OwnerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateMedium
import com.example.ui.theme.PureWhite
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoLight
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val viewModel: SalonViewModel = viewModel()
    val currentRole by viewModel.currentRole.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("role_navigation_bar"),
                containerColor = PureWhite,
                tonalElevation = 8.dp
            ) {
                // Role 1: Customer (Marketplace)
                NavigationBarItem(
                    selected = currentRole == "Customer",
                    onClick = { viewModel.switchRole("Customer") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Customer Portal",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Customer", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        unselectedIconColor = SlateMedium.copy(alpha = 0.5f),
                        unselectedTextColor = SlateMedium.copy(alpha = 0.5f),
                        indicatorColor = IndigoLight
                    ),
                    modifier = Modifier.testTag("nav_role_customer")
                )

                // Role 2: Owner (SaaS shop manager)
                NavigationBarItem(
                    selected = currentRole == "Salon Owner",
                    onClick = { viewModel.switchRole("Salon Owner") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Owner Dashboard",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Owner", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        unselectedIconColor = SlateMedium.copy(alpha = 0.5f),
                        unselectedTextColor = SlateMedium.copy(alpha = 0.5f),
                        indicatorColor = IndigoLight
                    ),
                    modifier = Modifier.testTag("nav_role_owner")
                )

                // Role 3: Barber (Stylist calendar schedule)
                NavigationBarItem(
                    selected = currentRole == "Barber",
                    onClick = { viewModel.switchRole("Barber") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Stylist schedule",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("Stylist", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        unselectedIconColor = SlateMedium.copy(alpha = 0.5f),
                        unselectedTextColor = SlateMedium.copy(alpha = 0.5f),
                        indicatorColor = IndigoLight
                    ),
                    modifier = Modifier.testTag("nav_role_barber")
                )

                // Role 4: Platform Admin (Global hub admin)
                NavigationBarItem(
                    selected = currentRole == "Platform Admin",
                    onClick = { viewModel.switchRole("Platform Admin") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "System Admin",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text("SysAdmin", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary,
                        unselectedIconColor = SlateMedium.copy(alpha = 0.5f),
                        unselectedTextColor = SlateMedium.copy(alpha = 0.5f),
                        indicatorColor = IndigoLight
                    ),
                    modifier = Modifier.testTag("nav_role_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentRole) {
                "Customer" -> CustomerScreen(viewModel)
                "Salon Owner" -> OwnerScreen(viewModel)
                "Barber" -> BarberScreen(viewModel)
                "Platform Admin" -> AdminScreen(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
