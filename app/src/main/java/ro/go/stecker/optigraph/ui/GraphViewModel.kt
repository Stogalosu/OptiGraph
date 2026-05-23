package ro.go.stecker.optigraph.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ro.go.stecker.optigraph.algs.completeGraphGenerator
import ro.go.stecker.optigraph.algs.randomGraphGenerator
import ro.go.stecker.optigraph.algs.randomTreeGenerator
import ro.go.stecker.optigraph.data.Edge
import ro.go.stecker.optigraph.data.GenerationType
import ro.go.stecker.optigraph.data.SelectionMode
import ro.go.stecker.optigraph.data.UiState
import ro.go.stecker.optigraph.ui.navigation.GraphMenus
import ro.go.stecker.optigraph.ui.screens.main.EditMenuTabs

class GraphViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun selectGenerationType(type: GenerationType) {
        _uiState.update { it.copy(selectedGeneration = type) }
    }

    fun selectDestination(destination: GraphMenus) {
        _uiState.update { it.copy(destination = destination) }
    }

    fun selectEditTab(tab: EditMenuTabs) {
        _uiState.update { it.copy(selectedEditTab = tab) }
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
    }

    fun addEdge(node: Int, cost: Int = 0) {
        if(_uiState.value.selectedNode == 0)
            _uiState.update { it.copy(selectedNode = node) }
        else {
            val edgeList = _uiState.value.edges
            val edge = Edge(
                a = _uiState.value.selectedNode,
                b = node,
                c = cost
            )
            edgeList.add(edge)
            _uiState.update { it.copy(edges = edgeList, selectedNode = 0, selectionMode = SelectionMode.None) }
        }
    }

    fun toggleSelectionMode(mode: SelectionMode) {
        if(_uiState.value.selectionMode == mode)
            _uiState.update { it.copy(selectionMode = SelectionMode.None) }
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
}