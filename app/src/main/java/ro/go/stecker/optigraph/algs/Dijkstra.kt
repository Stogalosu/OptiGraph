package ro.go.stecker.optigraph.algs

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ro.go.stecker.optigraph.data.Edge
import ro.go.stecker.optigraph.data.containsEdge
import java.util.PriorityQueue
import kotlin.time.Duration.Companion.seconds

data class Muchie (
    var nod:Int,
    var cost: Int
)

class DijkstraUiState(
    var parents: List<Int> = listOf(),
    var costs: List<Int> = listOf(),
    var currentNode: Int = 0,
    var finished: Boolean = false,
    var greenEdges: MutableList<Edge> = mutableListOf(),
    var redEdges: MutableList<Edge> = mutableListOf(),
    var blueEdges: MutableList<Edge> = mutableListOf()
) {
    fun copy() = DijkstraUiState(
        parents = parents.toMutableList(),
        costs = costs.toMutableList(),
        currentNode = currentNode,
        finished = finished,
        greenEdges = greenEdges.toMutableList(),
        redEdges = redEdges.toMutableList(),
        blueEdges = blueEdges.toMutableList()
    )

    fun nextIteration(node: Int, parentList: List<Int>, costList: List<Int>) {
        parents = parentList
        costs = costList
        currentNode = node
        for(edge in greenEdges.toList()) {
            blueEdges.add(edge)
            greenEdges.remove(edge)
        }
        redEdges.clear()
    }

    fun finish() {
        finished = true
    }

    fun getEdgeColor(edge: Edge): Color? {
        if(greenEdges.containsEdge(edge.a, edge.b)) return Color.Green
        if(redEdges.containsEdge(edge.a, edge.b)) return Color.Red
        if(blueEdges.containsEdge(edge.a, edge.b)) return Color.Blue
        return null
    }

    fun getNodeBorderColor(node: Int, root: Int): Color {
        if(node == currentNode) return Color.Blue
        if(node == root) return Color.Green
        return Color.Black
    }
}

fun dijkstra(v:MutableList<Edge>, n:Int, st:Int): Flow<DijkstraUiState> = flow {
    val a=Array(n+1){mutableListOf<Muchie>()}
    for(muc in v){
        a[muc.a].add(Muchie(muc.b,muc.c))
        a[muc.b].add(Muchie(muc.a,muc.c))
    }
    val d=IntArray(n+1){Int.MAX_VALUE}
    val t=IntArray(n+1){0}
    val pq= PriorityQueue<Muchie>(compareBy { it.cost })
    val snap = DijkstraUiState()
    d[st]=0
    pq.add(Muchie(st,0))
    while(pq.isNotEmpty()){
        val cur=pq.poll()
        val nod=cur!!.nod
        val cost=cur.cost
        if(cost>d[nod]) continue
        for(f in a[nod]){
            snap.nextIteration(nod, t.toMutableList(), d.toMutableList())
            emit(snap.copy())
            delay(1.seconds)
            val next =f.nod
            val nextcost=f.cost + d[nod]
            if(nextcost<d[next]){
                d[next]=nextcost
                pq.add(Muchie(next,nextcost))
                /// gasim muchia (nod next)care e mai buna
                snap.greenEdges.add(Edge(nod, next, f.cost))
                emit(snap.copy())
                delay(1.seconds)
                t[next]=nod
            } else {
                snap.redEdges.add(Edge(nod, next, f.cost))
                emit(snap.copy())
                delay(1.seconds)
            }
        }
    }
    snap.nextIteration(0, t.toMutableList(), d.toMutableList())
    snap.finish()
    emit(snap.copy())


}