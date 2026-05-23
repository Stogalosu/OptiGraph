package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel

@Composable
fun GraphNavBar(
    mainScreenNavController: NavHostController,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    val menuIcons = listOf(
        painterResource(R.drawable.rebase_edit_24px),
        painterResource(R.drawable.terminal_24px)
    )
    val menuLabels = listOf(
        stringResource(R.string.edit),
        stringResource(R.string.algorithms)
    )

    NavigationBar {
        GraphMenus.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = uiState.destination == destination,
                onClick = {
                    viewModel.selectDestination(destination)
                    mainScreenNavController.navigate(destination.name)
                },
                icon = { Icon(painter = menuIcons[index], contentDescription = null) },
                label = { Text(text = menuLabels[index]) }
            )
        }
    }
}