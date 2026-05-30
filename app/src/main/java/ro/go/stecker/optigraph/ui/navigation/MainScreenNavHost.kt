package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ro.go.stecker.optigraph.algs.DijkstraUiState
import ro.go.stecker.optigraph.algs.KruskalUiState
import ro.go.stecker.optigraph.ui.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.screens.main.AlgorithmMenu
import ro.go.stecker.optigraph.ui.screens.main.EditMenu

enum class GraphMenus {
    Edit,
    Algorithms
}

@Composable
fun MainScreenNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    dijkstraUiState: DijkstraUiState,
    kruskalUiState: KruskalUiState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    val screenWidth = LocalWindowInfo.current.containerSize.width

    NavHost(
        navController = navController,
        startDestination = GraphMenus.Edit.name
    ) {
        composable(
            route = GraphMenus.Edit.name,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetX = { -screenWidth }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    targetOffsetX = { -screenWidth }
                )
            }
        ) {
            EditMenu(
                snackbarHostState = snackbarHostState,
                uiState = uiState,
                viewModel = viewModel
            )
        }

        composable(
            route = GraphMenus.Algorithms.name,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    initialOffsetX = { screenWidth }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300),
                    targetOffsetX = { screenWidth }
                )
            }
        ) {
            AlgorithmMenu(
                uiState = uiState,
                dijkstraUiState = dijkstraUiState,
                kruskalUiState = kruskalUiState,
                viewModel = viewModel
            )
        }
    }
}