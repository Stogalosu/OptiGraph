package ro.go.stecker.optigraph.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.data.GenerationType
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel

enum class EditMenuTabs {
    Manual,
    Automatic
}

@Composable
fun EditMenu(
    uiState: UiState,
    viewModel: GraphViewModel
) {
    var selectedTab by remember { mutableStateOf(EditMenuTabs.Manual.ordinal) }
    var numberNodes by remember { mutableStateOf("") }
    val tabsText = listOf(
        stringResource(R.string.manual),
        stringResource(R.string.automatic)
    )

    Card(
        modifier = Modifier
//            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column() {
//            Spacer(modifier = Modifier.weight(1f))
            when(selectedTab) {
                0 -> {

                }
                1 -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp)
                    ) {
                        Column(modifier = Modifier.weight(2f)) {
                            OutlinedTextField(
                                value = numberNodes,
                                onValueChange = { numberNodes = it },
                                isError = numberNodes.toIntOrNull() == null,
                                singleLine = true,
                                label = { Text(stringResource(R.string.number_nodes)) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            RadioListItem(
                                text = stringResource(R.string.random_tree),
                                selected = uiState.selectedGeneration == GenerationType.RandomTree,
                                onClick = { viewModel.selectGenerationType(GenerationType.RandomTree) }
                            )
                            RadioListItem(
                                text = stringResource(R.string.complete_graph),
                                selected = uiState.selectedGeneration == GenerationType.CompleteGraph,
                                onClick = { viewModel.selectGenerationType(GenerationType.CompleteGraph) }
                            )
                            RadioListItem(
                                text = stringResource(R.string.random_graph),
                                selected = uiState.selectedGeneration == GenerationType.RandomGraph,
                                onClick = { viewModel.selectGenerationType(GenerationType.RandomGraph) }
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                if(numberNodes.toIntOrNull() != null && numberNodes.toIntOrNull() != 0) {
                                    viewModel.setNumberNodes(numberNodes.toInt())
                                    viewModel.generateGraph()
                                    /*TODO*/
                                    //Add snackbar when numebr is not good
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text(stringResource(R.string.generate)) }
                    }
                }
            }

//            Spacer(modifier = Modifier.weight(1f))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                divider = {}
            ) {
                EditMenuTabs.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = tabsText[index]) }
                    )
                }
            }
        }
    }
}

@Composable
fun RadioListItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            modifier = Modifier.padding(end = 16.dp, start = 0.dp, top = 0.dp, bottom = 0.dp)
        )
    }
}