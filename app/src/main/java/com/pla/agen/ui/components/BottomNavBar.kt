package com.pla.agen.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.pla.agen.ui.navigation.PlaRoute

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (PlaRoute) -> Unit
) {
    NavigationBar {
        NavItem(PlaRoute.Chat, currentRoute, onNavigate,
            Icons.Filled.Chat, "Ruang Percakapan dengan Gemini")
        NavItem(PlaRoute.Archive, currentRoute, onNavigate,
            Icons.Filled.Storage, "Arsip Memori Percakapan")
        NavItem(PlaRoute.Settings, currentRoute, onNavigate,
            Icons.Filled.Settings, "Pengaturan Aplikasi")
    }
}

@Composable
private fun NavItem(
    route: PlaRoute,
    currentRoute: String?,
    onNavigate: (PlaRoute) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String
) {
    NavigationBarItem(
        selected = currentRoute == route.route,
        onClick = { onNavigate(route) },
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(route.label) },
        modifier = Modifier.semantics { contentDescription = contentDesc }
    )
}
