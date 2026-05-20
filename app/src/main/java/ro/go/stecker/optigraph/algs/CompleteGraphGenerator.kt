package ro.go.stecker.optigraph.algs

import ro.go.stecker.optigraph.data.Edge

fun completeGraphGenerator(n: Int): MutableList<Edge> {
    val v = mutableListOf<Edge>()
    for (i in 1..n - 1) {
        for (j in i + 1..n) {
            v.add(Edge(i, j, (1..20).random()))
        }
    }
    return v
}