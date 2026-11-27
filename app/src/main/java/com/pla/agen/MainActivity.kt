package com.pla.agen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pla.agen.ui.components.BottomNavBar
import com.pla.agen.ui.navigation.PlaRoute
import com.pla.agen.ui.screens.archive.ArchiveScreen
import com.pla.agen.ui.screens.chat.ChatScreen
import com.pla.agen.ui.screens.settings.SettingsScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStack by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStack?.destination?.route

            Scaffold(
                bottomBar = {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route.route) {
                                popUpTo(PlaRoute.Chat.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = PlaRoute.Chat.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(PlaRoute.Chat.route) { ChatScreen() }
                    composable(PlaRoute.Archive.route) { ArchiveScreen() }
                    composable(PlaRoute.Settings.route) { SettingsScreen() }
                }
            }
        }
    }
}
