package com.pla.agen.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pla.agen.ui.screens.archive.ArchiveScreen
import com.pla.agen.ui.screens.chat.ChatScreen
import com.pla.agen.ui.screens.settings.SettingsScreen

sealed class PlaRoute(val route: String, val label: String) {
    data object Chat : PlaRoute("chat", "Ruang Percakapan")
    data object Archive : PlaRoute("archive", "Arsip Memori")
    data object Settings : PlaRoute("settings", "Pengaturan")
}

@Composable
fun PlaNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = PlaRoute.Chat.route) {
        composable(PlaRoute.Chat.route) { ChatScreen() }
        composable(PlaRoute.Archive.route) { ArchiveScreen() }
        composable(PlaRoute.Settings.route) { SettingsScreen() }
    }
}
