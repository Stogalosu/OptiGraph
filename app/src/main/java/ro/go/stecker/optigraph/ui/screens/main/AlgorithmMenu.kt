package ro.go.stecker.optigraph.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.algs.DijkstraUiState
import ro.go.stecker.optigraph.algs.KruskalUiState
import ro.go.stecker.optigraph.ui.UiState
import ro.go.stecker.optigraph.ui.GraphViewModel

enum class AlgorithmMenuTabs {
    Dijkstra,
    Kruskal
}

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

    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .fillMaxWidth()
            ) {
                if (uiState.nodes != 0) {
                    if ((uiState.isDijkstraTab() && !uiState.hasDijkstraRun) || (uiState.isKruskalTab() && !uiState.hasKruskalRun)) {
                        if (viewModel.isConnectedGraph()) {
                            when(uiState.selectedAlgorithmTab) {
                                AlgorithmMenuTabs.Dijkstra ->
                                    Text(
                                        text = stringResource(R.string.please_select_origin_node),
                                        fontWeight = FontWeight.Bold
                                    )

                                AlgorithmMenuTabs.Kruskal ->
                                    Button(
                                        onClick = { viewModel.startKruskal() }
                                    ) {
                                        Text(text = stringResource(R.string.run_kruskal))
                                    }
                            }
                        } else {
                            val algorithm =
                                when(uiState.selectedAlgorithmTab) {
                                    AlgorithmMenuTabs.Dijkstra -> stringResource(R.string.dijkstra)
                                    AlgorithmMenuTabs.Kruskal -> stringResource(R.string.kruskal)
                                }
                            Text(
                                text = stringResource(R.string.cannot_apply_algorithm, algorithm),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        when(uiState.selectedAlgorithmTab) {
                            AlgorithmMenuTabs.Dijkstra -> {
                                if (uiState.nodes <= 6) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = stringResource(R.string.nodes),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            repeat(uiState.nodes) {
                                                if (it + 1 != uiState.dijkstraRoot) {
                                                    Text(
                                                        text = stringResource(
                                                            R.string.from_x_to_y,
                                                            uiState.dijkstraRoot,
                                                            (it + 1).toString()
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = stringResource(R.string.cost),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            repeat(uiState.nodes) {
                                                if (it + 1 != uiState.dijkstraRoot) {
                                                    if (dijkstraUiState.costs.isNotEmpty()) {
                                                        val text =
                                                            if (dijkstraUiState.costs[it + 1] <= 1000) dijkstraUiState.costs[it + 1].toString()
                                                            else stringResource(R.string.infinity)
                                                        Text(text = text)
                                                    }
                                                }
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = stringResource(R.string.path),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            repeat(uiState.nodes) {
                                                if (it + 1 != uiState.dijkstraRoot) {
                                                    if (dijkstraUiState.parents.isNotEmpty())
                                                        Text(text = getPathToNode(it + 1, uiState.dijkstraRoot, dijkstraUiState.parents))
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    /*TODO*/
                                }
                                if (!dijkstraUiState.finished) {
                                    Text(
                                        text = stringResource(R.string.algorithm_is_running),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { viewModel.stopDijkstra() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text(text = stringResource(R.string.stop))
                                    }
                                } else {
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
                                        Text(text = stringResource(R.string.cost_is, kruskalUiState.cost.toString()))
                                        Text(text = stringResource(R.string.parents_are, kruskalUiState.parents.toString()))
                                    }
                                    if (!kruskalUiState.finished) {
                                        Text(
                                            text = stringResource(R.string.algorithm_is_running),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Button(
                                            onClick = { viewModel.stopKruskal() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Text(text = stringResource(R.string.stop))
                                        }
                                    } else {
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