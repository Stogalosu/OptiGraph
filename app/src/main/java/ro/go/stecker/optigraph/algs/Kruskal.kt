package ro.go.stecker.optigraph.algs

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import ro.go.stecker.optigraph.data.Edge
import ro.go.stecker.optigraph.data.connectsNodes
import kotlin.time.Duration

class KruskalUiState(
    var cost: Int = 0,
    var parents: List<Int> = listOf(),
    var finished: Boolean = false,
    var greenEdge: Edge = Edge(),
    var redEdge: Edge = Edge(),
    var blueEdges: MutableList<Edge> = mutableListOf(),
    var edgeToRemove: Edge = Edge(),
    var removedEdges: MutableList<Edge> = mutableListOf()
) {
    fun copy() = KruskalUiState(
        cost = cost,
        parents = parents.toList(),
        finished = finished,
        greenEdge = greenEdge,
        redEdge = redEdge,
        blueEdges = blueEdges.toMutableList(),
        edgeToRemove = edgeToRemove,
        removedEdges = removedEdges.toMutableList()
    )

    fun nextIteration(newCost: Int, parentList: List<Int>) {
        cost = newCost
        parents = parentList
        blueEdges.add(greenEdge)
        greenEdge = Edge()
        edgeToRemove = redEdge
        if(!edgeToRemove.connectsNodes(0, 0))
            removedEdges.add(edgeToRemove)
        redEdge = Edge()
    }

    fun getEdgeColor(edge: Edge): Color? {
        if(blueEdges.contains(edge)) return Color.Blue
        if(redEdge == edge) return Color.Red
        if(greenEdge == edge) return Color.Green
        return null
    }

    fun getNodeBorderColor(node: Int): Color? {
        for(edge in blueEdges) {
            if(edge.a == node || edge.b == node)
                return Color.Blue
        }
        return null
    }

    fun finish() {
        finished = true
    }
}

lateinit var t:IntArray

private fun root(x:Int): Int{
    if(x==t[x]) return x
    t[x]=root(t[x])
    return t[x]
}

fun kruskal(n: Int, v: MutableList<Edge>, delayFlow: StateFlow<Duration>): Flow<KruskalUiState> = flow {
    val snap = KruskalUiState()
    t=IntArray(n+1)
    for(i in 1..n){
        t[i]=i
    }
    v.sortBy {it.c}
    var cost=0
    for(muchie in v){
        val (nod1,nod2,cm)=muchie
        val ra=root(nod1)
        val rb=root(nod2)
        if(ra!=rb){
            t[ra]=rb
            snap.greenEdge = muchie
            emit(snap.copy())
            delay(delayFlow.value)
            cost+=cm
        } else {
            snap.redEdge = muchie
            emit(snap.copy())
            delay(delayFlow.value)
        }
        snap.nextIteration(cost, t.toList())
        emit(snap.copy())
        delay(delayFlow.value)
    }
    snap.finish()
    emit(snap.copy())
}