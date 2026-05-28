package ro.go.stecker.optigraph.algs

import ro.go.stecker.optigraph.data.Edge

fun genChain(n:Int, v: IntArray){
    val fol=IntArray(n+1){0}
    for(i in 1..n){
        var x=(1..n).random()
        while (fol[x]!=0){
            x=(1..n).random()
        }
        v[i]=x
        fol[x]=1
    }
}

fun randomGraphGenerator(n: Int): MutableList<Edge> {
    val graph = mutableListOf<Edge>()
    val v = IntArray(n+1){0}
    genChain(n, v)
    for (i in 1 until n){
        graph.add(Edge(v[i],v[i+1],(1..20).random()))
    }
    for(i in 1..n-1){
        for (j in i+1..n){
            if(j==i+1) continue
            else{
                val x=(1..100).random()
                if(x<45){
                    graph.add(Edge(v[i], v[j], (1..20).random()))
                }
            }
        }
    }
    return graph
}