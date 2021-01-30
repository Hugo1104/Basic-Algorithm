import java.io.*;
import java.util.*;
// Data structure for a node in a linked list
class Item {
    int data;
    Item next;

    Item(int data, Item next) {
        this.data = data;
        this.next = next;
    }
}

// Data structure for representing a graph
class Graph {
    int n;  // # of nodes in the graph

    Item[] A;
    // For u in [0..n), A[u] is the adjecency list for u

    Graph(int n) {
        // initialize a graph with n vertices and no edges
        this.n = n;
        A = new Item[n];
    }

    void addEdge(int u, int v) {
        // add an edge u -> v to the graph

        A[u] = new Item(v, A[u]);
    }
}

// Data structure holding data computed by DFS
class DFSInfo {

    // node colors
    static final int WHITE = 0;
    static final int GRAY  = 1;
    static final int BLACK = 2;

    int[] color;  // variable storing the color
    // of each node during DFS
    // (WHITE, GRAY, or BLACK)

    int[] parent; // variable storing the parent
    // of each node in the DFS forest

    int d[];      // variable storing the discovery time
    // of each node in the DFS forest

    int f[];      // variable storing the finish time
    // of each node in the DFS forest


    DFSInfo(Graph graph) {
        int n = graph.n;
        color = new int[n];
        parent = new int[n];
        d = new int[n];
        f = new int[n];
    }
}


// your "main program" should look something like this:

public class Solution {

    static int time = 0;

    static void recDFS(int u, Graph graph, DFSInfo info) {
        // perform a recursive DFS, starting at u
        info.color[u] = info.GRAY;
        info.d[u] = ++time;
        Item node = graph.A[u];
        while (node != null){
            if (info.color[node.data] == info.WHITE){
                info.parent[node.data] = u;
                recDFS(node.data, graph, info);
            }
            node = node.next;
        }
        info.color[u] = info.BLACK;
        info.f[u] = ++time;
    }

    static DFSInfo DFS(Graph graph) {
        // performs a "full" DFS on given graph
        DFSInfo info = new DFSInfo(graph);
        for (int j = 0; j < graph.n; j++) {
            info.color[j] = info.WHITE;
            info.parent[j] = j;
        }
        for (int i = 0; i < graph.n; i++) {
            if (info.color[i] == info.WHITE){
                recDFS(i, graph, info);
            }
        }
        return info;
    }

    static Item findCycle(Graph graph, DFSInfo info) {
        // If graph contains a cycle x_1 -> ... x_k -> x_1,
        // return a pointer to the head of the linked list
        // (x_1,..., x_k); otherwise, return null.
        // NOTE: if there is a cycle, you should just return
        // one cycle --- it does not matter which one.

        // To do this, scan through the edges of graph,
        // using info.f to locate a back edge.
        // Once you find a back edge, use info.parent
        // to build the list of nodes in the cycle
        // in the correct order.
        for (int i = 0; i < graph.n; i++) {
            Item u = graph.A[i];
            while (u != null){
                if (info.f[i] < info.f[u.data]){
                    Item cycleLast = new Item(i, u);
                    return cycleLast;
                }
                u = u.next;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        //take in input
        Scanner scanner = new Scanner(System.in);

        //create the graph
        int nodeSize = Integer.parseInt(scanner.next());
        int edgeSize = Integer.parseInt(scanner.next());
        Graph graph = new Graph(nodeSize);
        for (int i = 0; i < edgeSize; i++) {
            int u = Integer.parseInt(scanner.next());
            int v = Integer.parseInt(scanner.next());
            graph.addEdge(u - 1,v - 1);
        }

        //perform DFS
        DFSInfo info = DFS(graph);
        Item cycleLast = findCycle(graph, info);
        if (cycleLast != null){
            ArrayList<Integer> cycle = new ArrayList<Integer>();
            cycle.add(cycleLast.data);
            int parent = info.parent[cycleLast.data];
            while (parent != cycleLast.next.data){
                cycle.add(parent);
                parent = info.parent[parent];
            }
            cycle.add(parent);
            System.out.println(1);
            for (int i = cycle.size() - 1; i >= 0; i--) {
                System.out.print(cycle.get(i) + 1 + " ");
            }

        }else{
            System.out.println(0);
        }
    }

}
