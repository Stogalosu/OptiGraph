package ro.go.stecker.optigraph.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.ui.screens.main.discardRed

@Composable
fun DeleteGraphDialog(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onBackClick,
        icon = { Icon(painterResource(R.drawable.delete_forever_24px), contentDescription = null) },
        title = { Text(stringResource(R.string.delete_graph)) },
        text = { Text(stringResource(R.string.delete_graph_dialog)) },
        dismissButton = {
            TextButton(onClick = onBackClick) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(
                    text = stringResource(R.string.yes),
                    color = discardRed
                )
            }
        }
    )
}