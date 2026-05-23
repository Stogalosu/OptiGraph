package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ro.go.stecker.optigraph.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphTopAppBar(
    title: String,
    canGoBack: Boolean,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if(canGoBack)
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back_24px),
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
        }
    )
}