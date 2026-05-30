package ro.go.stecker.optigraph.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.algs.DijkstraUiState
import ro.go.stecker.optigraph.algs.KruskalUiState
import ro.go.stecker.optigraph.ui.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.navigation.GraphMenus

enum class AlgorithmMenuTabs {
    Dijkstra,
    Kruskal
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun AlgorithmMenu(
    dijkstraUiState: DijkstraUiState,
    kruskalUiState: KruskalUiState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    val tabsText = listOf(
        stringResource(R.string.dijkstra),
        stringResource(R.string.kruskal)
    )
    val infinitySymbol = stringResource(R.string.infinity)
    val context = LocalContext.current

    var nodeNumber by remember { mutableStateOf("") }

    LaunchedEffect(uiState.dijkstraRoot) {
        nodeNumber = uiState.dijkstraRoot.toString()
    }

    AnimatedVisibility(
        visible = uiState.destination == GraphMenus.Algorithms
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween())
                ) {
                    if (uiState.nodes != 0) {
                        if ((uiState.isDijkstraTab() && !uiState.hasDijkstraRun) || (uiState.isKruskalTab() && !uiState.hasKruskalRun)) {
                            if (viewModel.isConnectedGraph()) {
                                when (uiState.selectedAlgorithmTab) {
                                    AlgorithmMenuTabs.Dijkstra ->
                                        Text(
                                            text = stringResource(R.string.please_select_origin_node),
                                            fontWeight = FontWeight.Bold
                                        )

                                    AlgorithmMenuTabs.Kruskal -> {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .clickable { viewModel.toggleKruskalDeleteEdges() }
                                        ) {
                                            Checkbox(
                                                checked = uiState.kruskalDeleteEdges,
                                                onCheckedChange = { viewModel.toggleKruskalDeleteEdges() }
                                            )
                                            Text(text = stringResource(R.string.delete_unused_edges))
                                        }
                                        Button(
                                            onClick = { viewModel.startKruskal() }
                                        ) {
                                            Text(text = stringResource(R.string.run_kruskal))
                                        }
                                    }
                                }
                            } else {
                                val algorithm =
                                    when (uiState.selectedAlgorithmTab) {
                                        AlgorithmMenuTabs.Dijkstra -> stringResource(R.string.dijkstra)
                                        AlgorithmMenuTabs.Kruskal -> stringResource(R.string.kruskal)
                                    }
                                Text(
                                    text = stringResource(R.string.cannot_apply_algorithm, algorithm),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            when (uiState.selectedAlgorithmTab) {
                                AlgorithmMenuTabs.Dijkstra -> {
                                    if (uiState.nodes <= 6) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            DijkstraInfoColumn(
                                                title = stringResource(R.string.nodes),
                                                rowsText = {
                                                    context.resources.getString(
                                                        R.string.from_x_to_y,
                                                        uiState.dijkstraRoot.toString(),
                                                        it.toString()
                                                    )
                                                },
                                                uiState = uiState,
                                                dijkstraUiState = dijkstraUiState
                                            )
                                            DijkstraInfoColumn(
                                                title = stringResource(R.string.cost),
                                                rowsText = {
                                                    if (dijkstraUiState.costs[it] <= 1000) dijkstraUiState.costs[it].toString()
                                                    else infinitySymbol
                                                },
                                                uiState = uiState,
                                                dijkstraUiState = dijkstraUiState
                                            )
                                            DijkstraInfoColumn(
                                                title = stringResource(R.string.path),
                                                rowsText = {
                                                    getPathToNode(
                                                        it,
                                                        uiState.dijkstraRoot,
                                                        dijkstraUiState.parents
                                                    )
                                                },
                                                uiState = uiState,
                                                dijkstraUiState = dijkstraUiState
                                            )
                                        }
                                    }

                                    if (!dijkstraUiState.finished) {
                                        Text(
                                            text = stringResource(R.string.algorithm_is_running),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Button(
                                                onClick = { viewModel.skipDijkstra() }
                                            ) {
                                                Text(text = stringResource(R.string.skip_to_end))
                                            }
                                            Button(
                                                onClick = { viewModel.stopDijkstra() },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            ) {
                                                Text(text = stringResource(R.string.stop))
                                            }
                                        }
                                    } else {
                                        if (uiState.nodes > 6) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(
                                                        R.string.enter_node_dijkstra,
                                                        uiState.dijkstraRoot
                                                    )
                                                )
                                                IconButton(
                                                    onClick = {
                                                        if (nodeNumber.toIntOrNull() != null)
                                                            if (nodeNumber.toInt() > 1)
                                                                nodeNumber = (nodeNumber.toInt() - 1).toString()
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.remove_24px),
                                                        contentDescription = stringResource(R.string.previous_node)
                                                    )
                                                }
                                                OutlinedTextField(
                                                    value = nodeNumber,
                                                    onValueChange = { nodeNumber = it },
                                                    isError = nodeNumber.toIntOrNull() == null || (nodeNumber.toIntOrNull()
                                                        ?: 0) > uiState.nodes,
                                                    singleLine = true,
                                                    modifier = Modifier.width(52.dp)
                                                )
                                                IconButton(
                                                    onClick = {
                                                        if (nodeNumber.toIntOrNull() != null)
                                                            if (nodeNumber.toInt() < uiState.nodes)
                                                                nodeNumber = (nodeNumber.toInt() + 1).toString()
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.add_24px),
                                                        contentDescription = stringResource(R.string.next_node)
                                                    )
                                                }
                                            }
                                            if (nodeNumber.toIntOrNull() != null && (nodeNumber.toIntOrNull()
                                                    ?: 0) <= uiState.nodes
                                            )
                                                Row(
                                                    horizontalArrangement = Arrangement.Center,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text =
                                                            stringResource(
                                                                R.string.cost_is,
                                                                if (dijkstraUiState.costs[nodeNumber.toInt()] <= 1000)
                                                                    dijkstraUiState.costs[nodeNumber.toInt()].toString()
                                                                else infinitySymbol
                                                            ),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(16.dp))
                                                    Text(
                                                        text =
                                                            stringResource(
                                                                R.string.path_is,
                                                                getPathToNode(
                                                                    node = nodeNumber.toInt(),
                                                                    root = uiState.dijkstraRoot,
                                                                    parents = dijkstraUiState.parents
                                                                )
                                                            ),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            else
                                                Spacer(modifier = Modifier.height(32.dp))
                                        }

                                        Button(
                                            onClick = { viewModel.resetDijkstra() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text(text = stringResource(R.string.reset))
                                        }
                                    }
                                }

                                AlgorithmMenuTabs.Kruskal -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = stringResource(
                                                    R.string.cost_is,
                                                    kruskalUiState.cost.toString()
                                                )
                                            )
                                            Text(
                                                text = stringResource(
                                                    R.string.parents_are,
                                                    kruskalUiState.parents.toString()
                                                )
                                            )
                                        }
                                        if (!kruskalUiState.finished) {
                                            Text(
                                                text = stringResource(R.string.algorithm_is_running),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                Button(
                                                    onClick = { viewModel.skipKruskal() }
                                                ) {
                                                    Text(text = stringResource(R.string.skip_to_end))
                                                }
                                                Button(
                                                    onClick = { viewModel.stopKruskal() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                                ) {
                                                    Text(text = stringResource(R.string.stop))
                                                }
                                            }
                                        } else {
                                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                if (uiState.kruskalDeleteEdges)
                                                    Button(
                                                        onClick = { viewModel.restoreGraph() }
                                                    ) {
                                                        Text(text = stringResource(R.string.restore_graph))
                                                    }
                                                Button(
                                                    onClick = { viewModel.resetKruskal() },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                                ) {
                                                    Text(text = stringResource(R.string.reset))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else
                        Text(stringResource(R.string.you_need_graph))
                }

                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedAlgorithmTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    divider = {}
                ) {
                    AlgorithmMenuTabs.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = uiState.selectedAlgorithmTab.ordinal == index,
                            onClick = { viewModel.selectAlgorithmTab(AlgorithmMenuTabs.entries[index]) },
                            text = { Text(text = tabsText[index]) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DijkstraInfoColumn(
    title: String,
    rowsText: (Int) -> String,
    uiState: UiState,
    dijkstraUiState: DijkstraUiState
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        repeat(uiState.nodes) {
            if (it + 1 != uiState.dijkstraRoot) {
                if (dijkstraUiState.parents.isNotEmpty())
                    Text(text = rowsText(it + 1))
            }
        }
    }
}

fun getPathToNode(
    node: Int,
    root: Int,
    parents: List<Int>
): String {
    var currentNode = node
    var path = node.toString()
    while(parents[currentNode] != 0) {
        currentNode = parents[currentNode]
        path = "$currentNode, $path"
    }
    if(currentNode != root) return "-"
    else return path
}