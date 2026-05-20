package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.screens.main.EditMenu

enum class GraphMenus {
    Edit,
    Algorithms
}

@Composable
fun MainScreenNavHost(
    navController: NavHostController,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    NavHost(
        navController = navController,
        startDestination = GraphMenus.Edit.name
    ) {
        composable(route = GraphMenus.Edit.name) {
            EditMenu(
                uiState = uiState,
                viewModel = viewModel
            )
        }

        composable(route = GraphMenus.Algorithms.name) {

        }
    }
}