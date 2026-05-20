package ro.go.stecker.optigraph.data

import ro.go.stecker.optigraph.ui.navigation.GraphMenus

data class Edge(val x:Int, val y:Int, val c:Int)

enum class GenerationType {
    RandomTree,
    CompleteGraph,
    RandomGraph
}

data class UiState(
    val destination: GraphMenus = GraphMenus.Edit,
    var nodes: Int = 1,
    var selectedGeneration: GenerationType = GenerationType.RandomGraph,
    var edges: MutableList<Edge> = mutableListOf()
)
