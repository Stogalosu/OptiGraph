package ro.go.stecker.optigraph.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.ui.screens.main.discardRed

@Composable
fun EnterCostDialog(
    nodes: Pair<Int, Int>,
    onBackClick: () -> Unit,
    onConfirmClick: (Int) -> Unit
) {
    var cost by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onBackClick,
        title = { Text(stringResource(R.string.enter_cost)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.enter_cost_dialog, nodes.first.toString(), nodes.second.toString()))
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text(stringResource(R.string.enter_cost)) },
                    isError = cost.toIntOrNull() == null || cost.toIntOrNull() == 0,
                    singleLine = true
                )
                if(isError)
                    Text(
                        text = stringResource(R.string.please_enter_nonzero_number),
                        fontWeight = FontWeight.Bold
                    )
            }
        },
        dismissButton = {
            TextButton(onClick = onBackClick) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = discardRed
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if(cost.toIntOrNull() != null && cost.toIntOrNull() != 0)
                        onConfirmClick(cost.toInt())
                    else isError = true
                }
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
}