package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.screens.main.MainScreen

enum class GraphScreens {
    MainScreen,
    SideMenu
}

@Composable
fun GraphNavHost(
    navController: NavHostController,
    viewModel: GraphViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = GraphScreens.MainScreen.name,
        enterTransition = { slideInVertically(initialOffsetY = { it / 2 }) },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { -80 },
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            slideInVertically(
                initialOffsetY = { -80 },
                animationSpec = tween(durationMillis = 150)
            )
        },
        popExitTransition = {
            slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(
                tween(
                    durationMillis = 200,
                    delayMillis = 100
                )
            )
        }
    ) {
        composable(route = GraphScreens.MainScreen.name) {
            MainScreen(
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}
