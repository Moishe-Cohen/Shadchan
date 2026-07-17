package com.moishe.shadchan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.moishe.shadchan.data.AppSettings
import com.moishe.shadchan.ui.ShadchanViewModel
import com.moishe.shadchan.ui.screens.DashboardScreen
import com.moishe.shadchan.ui.screens.PeopleScreen
import com.moishe.shadchan.ui.screens.PersonFormScreen
import com.moishe.shadchan.ui.screens.PersonProfileScreen
import com.moishe.shadchan.ui.screens.SettingsScreen
import com.moishe.shadchan.ui.screens.SuggestionDetailScreen
import com.moishe.shadchan.ui.screens.SuggestionsScreen
import com.moishe.shadchan.ui.screens.WorkspaceScreen
import com.moishe.shadchan.ui.theme.ShadchanTheme

private sealed class BottomDest(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : BottomDest("dashboard", "בית", Icons.Default.Dashboard)
    object People : BottomDest("people", "אנשים", Icons.Default.People)
    object Workspace : BottomDest("workspace", "התאמה", Icons.Default.Handshake)
    object Suggestions : BottomDest("suggestions", "הצעות", Icons.Default.Favorite)
    object Settings : BottomDest("settings", "הגדרות", Icons.Default.Settings)
}

private val bottomDestinations = listOf(BottomDest.Dashboard, BottomDest.People, BottomDest.Workspace, BottomDest.Suggestions, BottomDest.Settings)

/** Root composable shared by the Android and desktop entry points. */
@Composable
fun App(viewModel: ShadchanViewModel) {
    val settings by viewModel.settings.collectAsState(initial = AppSettings())

    ShadchanTheme(themeMode = settings.themeMode, fontSize = settings.fontSize) {
        // Force RTL layout throughout, per spec, regardless of system locale.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(modifier = Modifier.fillMaxSize()) {
                ShadchanNavHost(viewModel)
            }
        }
    }
}

@Composable
private fun ShadchanNavHost(viewModel: ShadchanViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            NavigationBar {
                bottomDestinations.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRoute?.hierarchy?.any { it.route == dest.route } == true,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
