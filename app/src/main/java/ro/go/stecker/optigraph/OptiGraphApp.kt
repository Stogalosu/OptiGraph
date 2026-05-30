package ro.go.stecker.optigraph

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.screens.main.MainScreen

@Composable
fun OptiGraphApp(viewModel: GraphViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dijkstraUiState by viewModel.dijkstraUiState.collectAsState()
    val kruskalUiState by viewModel.kruskalUiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    MainScreen(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        dijkstraUiState = dijkstraUiState,
        kruskalUiState = kruskalUiState,
        viewModel = viewModel
    )
}