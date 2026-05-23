package ro.go.stecker.optigraph.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.ui.screens.main.discardRed

@Composable
fun RemoveNodeDialog(
    node: Int,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onBackClick,
        title = { Text(stringResource(R.string.remove_node)) },
        text = { Text(stringResource(R.string.remove_node_dialog, node.toString())) },
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