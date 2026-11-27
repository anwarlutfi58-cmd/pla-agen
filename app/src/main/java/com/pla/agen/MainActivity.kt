package com.pla.agen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pla.agen.ui.components.BottomNavBar
import com.pla.agen.ui.navigation.PlaNavHostWithController
import com.pla.agen.ui.theme.PlaAgenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaAgenTheme {
                val nav = rememberNavController()
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination?.route

                Scaffold(
                    bottomBar = {
                        BottomNavBar(current) { route ->
                            nav.navigate(route.route) {
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        PlaNavHostWithController(nav)
                    }
                }
            }
        }
    }
}
