package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import ro.go.stecker.optigraph.R

@Composable
fun GraphNavBar(
    mainScreenNavController: NavHostController
) {
    var activeDestination by remember { mutableStateOf(GraphMenus.Edit) }
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
                selected = activeDestination == destination,
                onClick = {
                    activeDestination = destination
                    mainScreenNavController.navigate(activeDestination.name)
                },
                icon = { Icon(painter = menuIcons[index], contentDescription = null) },
                label = { Text(text = menuLabels[index]) }
            )
        }
    }
}