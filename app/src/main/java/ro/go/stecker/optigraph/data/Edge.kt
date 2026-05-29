package ro.go.stecker.optigraph.data

data class Edge(
    var a: Int = 0,
    var b: Int = 0,
    var c: Int = 0
)

fun Edge.connectsNodes(a: Int, b: Int): Boolean {
    return (this.a == a && this.b == b) || (this.a == b && this.b == a)
}

fun Edge.decrementFirstNode() {
    this.a--
}

fun Edge.decrementSecondNode() {
    this.b--
}

fun MutableList<Edge>.containsEdge(a: Int, b: Int): Boolean {
    for(edge in this) {
        if((edge.a == a && edge.b == b) || (edge.a == b && edge.b == a))
            return true
    }
    return false
}