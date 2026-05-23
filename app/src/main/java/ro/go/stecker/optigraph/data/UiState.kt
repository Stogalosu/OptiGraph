package ro.go.stecker.optigraph.data

import ro.go.stecker.optigraph.ui.navigation.GraphMenus
import ro.go.stecker.optigraph.ui.screens.main.EditMenuTabs

class Edge(var a:Int, var b:Int, var c:Int) {
    fun decrementFirstNode() {
        a--
    }
    fun decrementSecondNode() {
        b--
    }
}

fun MutableList<Edge>.containsEdge(node1: Int, node2: Int): Boolean {
    for(edge in this) {
        if((edge.a == node1 && edge.b == node2) || (edge.a == node2 && edge.b == node1))
            return true
    }
    return false
}

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

data class UiState(
    val destination: GraphMenus = GraphMenus.Edit,
    val selectedEditTab: EditMenuTabs = EditMenuTabs.Manual,
    val nodes: Int = 0,
    val selectedGeneration: GenerationType = GenerationType.RandomGraph,
    val edges: MutableList<Edge> = mutableListOf(),
    val selectionMode: SelectionMode = SelectionMode.None,
    val selectedNode: Int = 0
)
