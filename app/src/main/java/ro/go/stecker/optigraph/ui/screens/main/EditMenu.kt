package ro.go.stecker.optigraph.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.data.GenerationType
import ro.go.stecker.optigraph.data.SelectionMode
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel

enum class EditMenuTabs {
    Manual,
    Automatic
}

@Composable
fun EditMenu(
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    val tabsText = listOf(
        stringResource(R.string.manual),
        stringResource(R.string.automatic)
    )

    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column() {
            when(uiState.selectedEditTab) {
                EditMenuTabs.Manual ->
                    ManualEditMenu(
                        snackbarHostState = snackbarHostState,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                EditMenuTabs.Automatic ->
                    AutomaticEditMenu(
                        snackbarHostState = snackbarHostState,
                        uiState = uiState,
                        viewModel = viewModel
                    )
            }

            PrimaryTabRow(
                selectedTabIndex = uiState.selectedEditTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                divider = {}
            ) {
                EditMenuTabs.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedEditTab.ordinal == index,
                        onClick = { viewModel.selectEditTab(EditMenuTabs.entries[index]) },
                        text = { Text(text = tabsText[index]) }
                    )
                }
            }
        }
    }
}

@Composable
fun ManualEditMenu(
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var numberNodes by remember { mutableStateOf("1") }
    val noNodesRemoveText = stringResource(R.string.no_nodes_to_remove)
    val noNodesEdgesText = stringResource(R.string.no_nodes_for_edges)
    val noEdgesText = stringResource(R.string.no_edges_to_remove)

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp)
    ) {
        when(uiState.selectionMode) {
            SelectionMode.None ->
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.nodes),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.edges),
                        fontWeight = FontWeight.Bold
                    )
                }

            SelectionMode.RemoveNode ->
                Text(
                    text = stringResource(R.string.click_node_to_remove),
                    fontWeight = FontWeight.Bold
                )

            SelectionMode.AddEdge ->
                if(uiState.selectedNode == 0)
                    Text(
                        text = stringResource(R.string.please_select_two_nodes),
                        fontWeight = FontWeight.Bold
                    )
                else
                    Text(
                        text = stringResource(R.string.you_selected_node_x, uiState.selectedNode),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

            else -> {}
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { numberNodes = (numberNodes.toInt() + 1).toString() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_24px),
                            contentDescription = stringResource(R.string.add_more_nodes)
                        )
                    }
                    OutlinedTextField(
                        value = numberNodes,
                        onValueChange = { numberNodes = it },
                        isError = numberNodes.toIntOrNull() == null,
                        singleLine = true,
                        modifier = Modifier.width(52.dp)
                    )
                    IconButton(
                        onClick = {
                            if (numberNodes.toInt() > 1)
                                numberNodes = (numberNodes.toInt() - 1).toString()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.remove_24px),
                            contentDescription = stringResource(R.string.add_less_nodes)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.setNumberNodes(uiState.nodes + numberNodes.toInt())
                    }
                ) {
                    Icon(painterResource(R.drawable.add_24px), contentDescription = null)
                    if((numberNodes.toIntOrNull() ?: 0) == 1)
                        Text(stringResource(R.string.add_node))
                    else
                        Text(stringResource(R.string.add_x_nodes, numberNodes))
                }
                Button(
                    onClick = {
                        if(uiState.nodes != 0)
                            viewModel.toggleSelectionMode(SelectionMode.RemoveNode)
                        else
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(noNodesRemoveText)
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(painterResource(R.drawable.remove_24px), contentDescription = null)
                    Text(stringResource(R.string.remove_node))
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        if(uiState.nodes > 1)
                            viewModel.toggleSelectionMode(SelectionMode.AddEdge)
                        else
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(noNodesEdgesText)
                            }
                    }
                ) {
                    Icon(painterResource(R.drawable.add_24px), contentDescription = null)
                    Text(stringResource(R.string.add_edge))
                }
                Button(
                    onClick = {
                        if(uiState.edges.isNotEmpty())
                            viewModel.toggleSelectionMode(SelectionMode.RemoveEdge)
                        else
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(noEdgesText)
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(painterResource(R.drawable.remove_24px), contentDescription = null)
                    Text(stringResource(R.string.remove_edge))
                }
            }
        }
    }
}

@Composable
fun AutomaticEditMenu(
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    var numberNodes by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

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

        val pleaseEnterNumberText = stringResource(R.string.please_enter_number)

        Button(
            onClick = {
                if(numberNodes.toIntOrNull() != null && (numberNodes.toIntOrNull() ?: 0) > 1) {
                    viewModel.setNumberNodes(numberNodes.toInt())
                    viewModel.generateGraph()
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(pleaseEnterNumberText)
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.generate)) }
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