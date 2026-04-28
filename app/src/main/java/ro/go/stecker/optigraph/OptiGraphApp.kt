package ro.go.stecker.optigraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ro.go.stecker.optigraph.ui.navigation.GraphNavHost

@Composable
fun OptiGraphApp(navController: NavHostController = rememberNavController()) {
    GraphNavHost(navController)
}