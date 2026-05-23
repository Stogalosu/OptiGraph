package ro.go.stecker.optigraph.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ro.go.stecker.optigraph.R
import ro.go.stecker.optigraph.data.SelectionMode
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.data.containsEdge
import ro.go.stecker.optigraph.getActivity
import ro.go.stecker.optigraph.ui.GraphViewModel
import ro.go.stecker.optigraph.ui.Node
import ro.go.stecker.optigraph.ui.dialogs.EnterCostDialog
import ro.go.stecker.optigraph.ui.dialogs.RemoveNodeDialog
import ro.go.stecker.optigraph.ui.layout.CircularLayout
import ro.go.stecker.optigraph.ui.navigation.GraphNavBar
import ro.go.stecker.optigraph.ui.navigation.GraphTopAppBar
import ro.go.stecker.optigraph.ui.navigation.MainScreenNavHost
import kotlin.math.atan
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

val discardRed = Color(224, 65, 65)

@Composable
fun MainScreen(
    mainScreenNavController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
    uiState: UiState,
    viewModel: GraphViewModel
) {
    Scaffold(
        topBar = {
            GraphTopAppBar(
                title = stringResource(R.string.main_screen),
                canGoBack = uiState.selectionMode != SelectionMode.None,
                onBackClick = { viewModel.toggleSelectionMode(uiState.selectionMode) }
            )
         },
        bottomBar = {
            GraphNavBar(
                mainScreenNavController = mainScreenNavController,
                uiState = uiState,
                viewModel = viewModel
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val density = LocalDensity.current
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val nodeRadiusDp = 16.dp
        val nodeRadiusPx = with(density) { nodeRadiusDp.toPx() }
        var canvasSize by remember { mutableStateOf(IntSize.Zero) }

        val offsets = remember {
            MutableList(200) { mutableStateOf(Offset(-nodeRadiusPx, -nodeRadiusPx)) }
        }
        var coords = remember {
            MutableList(200) { mutableStateOf(Offset(0f, 0f)) }
        }
        var centerCoords by remember { mutableStateOf(Offset(0f, 0f)) }

        var removeNodeDialog by remember { mutableStateOf(0) }
        var enterCostDialog by remember { mutableStateOf(0) }

        val differentNodeText = stringResource(R.string.please_select_another_node)
        val thisEdgeExistsText = stringResource(R.string.this_edge_exists)

        fun nodeBoundsX(nodeIndex: Int, minMax: Boolean, edge: Boolean): Float {
            return when(minMax) {
                false ->
                    when(edge) {
                        false -> centerCoords.x - canvasSize.width/2 - coords[nodeIndex].value.x + offsets[nodeIndex].value.x
                        true -> centerCoords.x - canvasSize.width/2
                    }
                true ->
                    when(edge) {
                        false -> centerCoords.x + canvasSize.width/2 - coords[nodeIndex].value.x + offsets[nodeIndex].value.x
                        true -> centerCoords.x + canvasSize.width/2
                    }
            }
        }
        fun nodeBoundsY(nodeIndex: Int, minMax: Boolean, edge: Boolean): Float {
            return when(minMax) {
                false ->
                    when(edge) {
                        false -> centerCoords.y - canvasSize.height/2 - coords[nodeIndex].value.y + offsets[nodeIndex].value.y
                        true -> centerCoords.y - canvasSize.height/2
                    }
                true ->
                    when(edge) {
                        false -> centerCoords.y + canvasSize.height/2 - coords[nodeIndex].value.y + offsets[nodeIndex].value.y
                        true -> centerCoords.y + canvasSize.height/2
                    }
            }
        }

        BackHandler {
            if(uiState.selectionMode == SelectionMode.None) context.getActivity()?.finish()
            else viewModel.toggleSelectionMode(uiState.selectionMode)
        }

        LaunchedEffect(uiState.nodes, uiState.edges) {
            offsets.forEach { it.value = Offset(-nodeRadiusPx, -nodeRadiusPx) }
        }

        LaunchedEffect(uiState.destination, uiState.selectedEditTab) {
            delay(100.milliseconds)
            /*TODO*/
            viewModel.toggleSelectionMode(uiState.selectionMode)
        }

        if(removeNodeDialog != 0)
            RemoveNodeDialog(
                node = removeNodeDialog,
                onBackClick = { removeNodeDialog = 0 },
                onConfirmClick = {
                    viewModel.removeNode(removeNodeDialog)
                    removeNodeDialog = 0
                }
            )

        if(enterCostDialog != 0)
            EnterCostDialog(
                nodes = Pair(uiState.selectedNode, enterCostDialog),
                onBackClick = {
                    viewModel.setSelectedNode(0)
                    viewModel.toggleSelectionMode(uiState.selectionMode)
                    enterCostDialog = 0
                },
                onConfirmClick = { cost ->
                    viewModel.addEdge(enterCostDialog, cost)
                    enterCostDialog = 0
                }
            )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if(uiState.nodes != 0) {
                Box(
                    modifier = Modifier.onSizeChanged {
                        canvasSize = it
                    }
                ) {
                    Canvas(
                        modifier = Modifier
                            .padding(nodeRadiusDp)
                    ) {
                        val rectWidth = with(density) { 32.dp.toPx() }
                        val rectHeight = with(density) { 16.dp.toPx() }

                        for (edge in uiState.edges) {
                            val coords1 = Offset(
                                coords[edge.a - 1].value.x.coerceIn(
                                    nodeBoundsX(edge.a - 1, false, true),
                                    nodeBoundsX(edge.a - 1, true, true)
                                ),
                                coords[edge.a - 1].value.y.coerceIn(
                                    nodeBoundsY(edge.a - 1, false, true),
                                    nodeBoundsY(edge.a - 1, true, true)
                                )
                            )
                            val coords2 = Offset(
                                coords[edge.b - 1].value.x.coerceIn(
                                    nodeBoundsX(edge.b - 1, false, true),
                                    nodeBoundsX(edge.b - 1, true, true)
                                ),
                                coords[edge.b - 1].value.y.coerceIn(
                                    nodeBoundsY(edge.b - 1, false, true),
                                    nodeBoundsY(edge.b - 1, true, true)
                                )
                            )
                            drawLine(
                                start = coords1,
                                end = coords2,
                                color = Color.Magenta,
                                strokeWidth = 5F
                            )

                            val rectCenterX = (coords1.x + coords2.x)
                            val rectCenterY = (coords1.y + coords2.y)
                            val rectOffset = Offset(
                                rectCenterX / 2 - rectWidth / 2,
                                rectCenterY / 2 - rectHeight / 2
                            )

                            rotate(
                                degrees = atan((coords2.y - coords1.y) / (coords2.x - coords1.x)) * (180f / Math.PI.toFloat()),
                                pivot = Offset(rectOffset.x + rectWidth / 2, rectOffset.y + rectHeight / 2)
                            ) {
                                drawRoundRect(
                                    color = Color.Red,
                                    topLeft = rectOffset,
                                    size = Size(rectWidth, rectHeight),
                                    cornerRadius = CornerRadius(5f, 5f)
                                )
                            }
                        }
                    }

                    CircularLayout(
                        radius = 150.dp,
                        startAngle = 270f,
                        onCoordsChange = { index, x, y ->
                            coords[index].value =
                                Offset(x, y) + offsets[index].value - Offset(-nodeRadiusPx, -nodeRadiusPx)
                        },
                        onCenterCoordsChange = { x, y ->
                            centerCoords = Offset(x, y)
                        },
                        modifier = Modifier
                            .padding(nodeRadiusDp)
                    ) {
                        repeat(uiState.nodes) {
                            Node(
                                radius = nodeRadiusDp,
                                text = (it + 1).toString(),
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            offsets[it].value.x.roundToInt().coerceIn(
                                                nodeBoundsX(it, false, false).toInt(),
                                                nodeBoundsX(it, true, false).toInt()
                                            ),
                                            offsets[it].value.y.roundToInt().coerceIn(
                                                nodeBoundsY(it, false, false).toInt(),
                                                nodeBoundsY(it, true, false).toInt()
                                            ),
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            offsets[it].value += dragAmount
                                            coords[it].value += dragAmount
                                        }
                                    }
                                    .clickable(
                                        enabled = uiState.selectionMode == SelectionMode.RemoveNode || uiState.selectionMode == SelectionMode.AddEdge,
                                        onClick = {
                                            if(uiState.selectionMode == SelectionMode.RemoveNode)
                                                removeNodeDialog = it + 1
                                            else {
                                                if(uiState.selectedNode == 0)
                                                    viewModel.addEdge(it + 1)
                                                else {
                                                    if(uiState.selectedNode == it + 1)
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar(differentNodeText)
                                                        }
                                                    else if(uiState.edges.containsEdge(uiState.selectedNode, it +1))
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar(thisEdgeExistsText + differentNodeText)
                                                        }
                                                    else enterCostDialog = it + 1
                                                }
                                            }
                                        }
                                    )
                            )
                        }
                    }
                }
            } else {
                Text(stringResource(R.string.nothing_show))
            }

            Spacer(modifier = Modifier.weight(1f))

            MainScreenNavHost(
                navController = mainScreenNavController,
                snackbarHostState = snackbarHostState,
                uiState = uiState,
                viewModel = viewModel
            )
        }
    }
}