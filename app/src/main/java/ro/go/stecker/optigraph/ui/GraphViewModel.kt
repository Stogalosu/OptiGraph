package ro.go.stecker.optigraph.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ro.go.stecker.optigraph.algs.DijkstraUiState
import ro.go.stecker.optigraph.algs.KruskalUiState
import ro.go.stecker.optigraph.algs.completeGraphGenerator
import ro.go.stecker.optigraph.algs.dijkstra
import ro.go.stecker.optigraph.algs.kruskal
import ro.go.stecker.optigraph.algs.randomGraphGenerator
import ro.go.stecker.optigraph.algs.randomTreeGenerator
import ro.go.stecker.optigraph.data.Edge
import ro.go.stecker.optigraph.data.connectsNodes
import ro.go.stecker.optigraph.data.decrementFirstNode
import ro.go.stecker.optigraph.data.decrementSecondNode
import ro.go.stecker.optigraph.ui.navigation.GraphMenus
import ro.go.stecker.optigraph.ui.screens.main.AlgorithmMenuTabs
import ro.go.stecker.optigraph.ui.screens.main.EditMenuTabs
import kotlin.time.Duration.Companion.seconds

class GraphViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val _dijkstraUiState = MutableStateFlow(DijkstraUiState())
    val dijkstraUiState = _dijkstraUiState.asStateFlow()
    var dijkstraJob: Job = Job()
    private val _kruskalUiState = MutableStateFlow(KruskalUiState())
    val kruskalUiState = _kruskalUiState.asStateFlow()
    var kruskalJob: Job = Job()

    fun selectGenerationType(type: GenerationType) {
        _uiState.update { it.copy(selectedGeneration = type) }
    }

    fun selectDestination(destination: GraphMenus) {
        _uiState.update { it.copy(destination = destination) }
    }

    fun selectEditTab(tab: EditMenuTabs) {
        _uiState.update { it.copy(selectedEditTab = tab) }
    }

    fun selectAlgorithmTab(tab: AlgorithmMenuTabs) {
        _uiState.update { it.copy(selectedAlgorithmTab = tab) }
    }

    fun setNumberNodes(number: Int) {
        _uiState.update { it.copy(nodes = number) }
    }

    fun removeNode(node: Int) {
        val edges = _uiState.value.edges
        for (edge in edges.toList()) {
            if (edge.a == node || edge.b == node)
                edges.remove(edge)
            else {
                if (edge.a > node) edge.decrementFirstNode()
                if (edge.b > node) edge.decrementSecondNode()
            }
        }
        _uiState.update { it.copy(nodes = it.nodes - 1, edges = edges, selectionMode = SelectionMode.None) }
    }

    fun setSelectedNode(node: Int) {
        _uiState.update { it.copy(selectedNode = node) }
        if(node != 0 && uiState.value.isDijkstraTab())
            startDijkstra(node)
    }

    fun editEdge(node: Int, cost: Int = 0) {
        if(_uiState.value.selectedNode == 0)
            setSelectedNode(node)
        else {
            val edgeList = _uiState.value.edges.toMutableList()
            if(_uiState.value.selectionMode == SelectionMode.AddEdge) {
                val edge = Edge(
                    a = _uiState.value.selectedNode,
                    b = node,
                    c = cost
                )
                edgeList.add(edge)
            }
            else if(_uiState.value.selectionMode == SelectionMode.RemoveEdge)
                edgeList.removeIf { it.connectsNodes(_uiState.value.selectedNode, node) }

            _uiState.update { it.copy(edges = edgeList, selectedNode = 0, selectionMode = SelectionMode.None) }
        }
    }

    fun toggleSelectionMode(mode: SelectionMode) {
        if(_uiState.value.selectionMode == mode)
            _uiState.update { it.copy(selectionMode = SelectionMode.None, selectedNode = 0) }
        else _uiState.update { it.copy(selectionMode = mode) }
    }

    fun generateGraph() {
        when(_uiState.value.selectedGeneration) {
            GenerationType.RandomTree -> {
                _uiState.update { it.copy(edges = randomTreeGenerator(_uiState.value.nodes)) }
            }
            GenerationType.CompleteGraph -> {
                _uiState.update { it.copy(edges = completeGraphGenerator(_uiState.value.nodes)) }
            }
            GenerationType.RandomGraph -> {
                _uiState.update { it.copy(edges = randomGraphGenerator(_uiState.value.nodes)) }
            }
        }
    }

    fun deleteGraph() {
        if(_uiState.value.hasDijkstraRun) resetDijkstra()
        _uiState.update { it.copy(nodes = 0, edges = mutableListOf()) }
    }

    fun isConnectedGraph(): Boolean {
        return true
    }

    var dijkstraDelay = MutableStateFlow(1.seconds)
    fun startDijkstra(root: Int) {
        _uiState.update { it.copy(hasDijkstraRun = true, dijkstraRoot = root) }
        dijkstraJob = viewModelScope.launch {
            dijkstra(
                _uiState.value.edges,
                _uiState.value.nodes,
                _uiState.value.dijkstraRoot,
                dijkstraDelay
            ).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = DijkstraUiState()
            ).collect { newState ->
                _dijkstraUiState.value = newState
            }
        }
    }

    fun skipDijkstra() {
        dijkstraDelay.value = 0.seconds
        dijkstraJob.invokeOnCompletion { dijkstraDelay.value = 1.seconds }
    }

    fun stopDijkstra() {
        dijkstraJob.cancel()
        _dijkstraUiState.update { it.copy().also { state -> state.finished = true } }
    }

    fun resetDijkstra() {
        _uiState.update { it.copy(hasDijkstraRun = false, selectedNode = 0) }
        stopDijkstra()
        _dijkstraUiState.value = DijkstraUiState()
    }

    var kruskalDelay = MutableStateFlow(1.seconds)
    fun startKruskal() {
        _uiState.update { it.copy(hasKruskalRun = true) }
        kruskalJob = viewModelScope.launch {
            kruskal(
                _uiState.value.nodes,
                _uiState.value.edges,
                kruskalDelay
            ).stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = KruskalUiState()
            ).collect { newState ->
                _kruskalUiState.value = newState
                if(_uiState.value.kruskalDeleteEdges && kruskalDelay.value > 0.seconds) {
                    val edgeList = _uiState.value.edges.toMutableList()
                    edgeList.removeIf { it == _kruskalUiState.value.edgeToRemove }
                    _uiState.update { it.copy(edges = edgeList) }
                }
                if(newState.finished) kruskalJob.cancel()
            }
        }
    }

    fun skipKruskal() {
        kruskalJob.invokeOnCompletion { kruskalDelay.value = 1.seconds }
        if(_uiState.value.kruskalDeleteEdges)
            kruskalJob.invokeOnCompletion {
                val edgeList = _uiState.value.edges.toMutableList()
                edgeList.removeIf { _kruskalUiState.value.removedEdges.contains(it) }
                _uiState.update { it.copy(edges = edgeList) }
            }
        kruskalDelay.value = 0.seconds
    }

    fun stopKruskal() {
        kruskalJob.cancel()
        _kruskalUiState.update { it.copy().also { state -> state.finished = true } }
    }

    fun resetKruskal() {
        _uiState.update { it.copy(hasKruskalRun = false) }
        stopKruskal()
        _kruskalUiState.value = KruskalUiState()
    }

    fun toggleKruskalDeleteEdges() {
        _uiState.update { it.copy(kruskalDeleteEdges = !it.kruskalDeleteEdges) }
    }

    fun restoreGraph() {
        val edgeList = _uiState.value.edges.toMutableList()
        _kruskalUiState.value.removedEdges.forEach {
            edgeList.add(it)
        }
        _uiState.update { it.copy(edges = edgeList) }
        resetKruskal()
    }
}