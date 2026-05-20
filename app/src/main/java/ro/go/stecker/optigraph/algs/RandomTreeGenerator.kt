package ro.go.stecker.optigraph.algs

import ro.go.stecker.optigraph.data.Edge


fun randomTreeGenerator(n: Int): MutableList<Edge>{
    val v= mutableListOf<Edge>()
    val t=IntArray(n+1){0}
    for(i in 2..n){
        t[i]=(1..i).random()
        while(t[i]==i){
            t[i]=(1..i).random()
        }
    }
    for(i in 2..n){
        ///println("${t[i]}  $i")
        v.add(Edge(t[i],i,(1..20).random()))
    }
    return v
}