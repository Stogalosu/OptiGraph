package ro.go.stecker.optigraph.data

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