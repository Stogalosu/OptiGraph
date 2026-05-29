package ro.go.stecker.optigraph.ui

import androidx.compose.ui.graphics.Color
import ro.go.stecker.optigraph.data.Edge
import ro.go.stecker.optigraph.ui.navigation.GraphMenus
import ro.go.stecker.optigraph.ui.screens.main.AlgorithmMenuTabs
import ro.go.stecker.optigraph.ui.screens.main.EditMenuTabs

enum class GenerationType {
    RandomTree,
    CompleteGraph,
    RandomGraph
}

enum class SelectionMode {
    None,
    RemoveNode,
    AddEdge,
    RemoveEdge
}

class UiState(
    val destination: GraphMenus = GraphMenus.Edit,
    val selectedEditTab: EditMenuTabs = EditMenuTabs.Manual,
    val selectedAlgorithmTab: AlgorithmMenuTabs = AlgorithmMenuTabs.Dijkstra,
    val hasDijkstraRun: Boolean = false,
    val dijkstraRoot: Int = 0,
    val hasKruskalRun: Boolean = false,
    val nodes: Int = 0,
    val selectedGeneration: GenerationType = GenerationType.RandomGraph,
    val edges: MutableList<Edge> = mutableListOf(),
    val selectionMode: SelectionMode = SelectionMode.None,
    val selectedNode: Int = 0
) {
    fun copy(
        destination: GraphMenus = this.destination,
        selectedEditTab: EditMenuTabs = this.selectedEditTab,
        selectedAlgorithmTab: AlgorithmMenuTabs = this.selectedAlgorithmTab,
        hasDijkstraRun: Boolean = this.hasDijkstraRun,
        dijkstraRoot: Int = this.dijkstraRoot,
        hasKruskalRun: Boolean = this.hasKruskalRun,
        nodes: Int = this.nodes,
        selectedGeneration: GenerationType = this.selectedGeneration,
        edges: MutableList<Edge> = this.edges,
        selectionMode: SelectionMode = this.selectionMode,
        selectedNode: Int = this.selectedNode
    ): UiState {
        return UiState(
            destination,
            selectedEditTab,
            selectedAlgorithmTab,
            hasDijkstraRun,
            dijkstraRoot,
            hasKruskalRun,
            nodes,
            selectedGeneration,
            edges,
            selectionMode,
            selectedNode
        )
    }

    fun isDijkstraTab(): Boolean {
        return destination == GraphMenus.Algorithms && selectedAlgorithmTab == AlgorithmMenuTabs.Dijkstra
    }

    fun isKruskalTab(): Boolean {
        return destination == GraphMenus.Algorithms && selectedAlgorithmTab == AlgorithmMenuTabs.Kruskal
    }

    fun areNodesClickable(): Boolean {
        return selectionMode != SelectionMode.None ||
                (isDijkstraTab() && !hasDijkstraRun)
    }

    fun nodeBorderColor(node: Int): Color {
        if(selectedNode == node) {
            if(selectionMode == SelectionMode.AddEdge) return Color.Green
            if(selectionMode == SelectionMode.RemoveEdge) return Color.Red
        }
        return Color.Black
    }
}