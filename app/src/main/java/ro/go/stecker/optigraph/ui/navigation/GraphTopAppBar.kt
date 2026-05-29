package ro.go.stecker.optigraph.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ro.go.stecker.optigraph.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphTopAppBar(
    title: String,
    canGoBack: Boolean,
    onBackClick: () -> Unit = {},
    onDeleteGraphClick: () -> Unit
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
        },
        actions = {
            SmallFloatingActionButton(
                onClick = onDeleteGraphClick,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete_forever_24px),
                    contentDescription = stringResource(R.string.delete_graph)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}