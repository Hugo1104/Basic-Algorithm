import java.util.Scanner;
import java.util.stream.IntStream;

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
    int k;
    // # of trees in DFS forest

    int[] T;
    // For u in [0..n), T[u] is initially 0, but when DFS discovers
    // u, T[u] is set to the index (which is in [1..k]) of the tree
    // in DFS forest in which u belongs.

    int[] L;
    // List of nodes in order of decreasing finishing time

    int count;
    // initially set to n, and is decremented every time
    // DFS finishes with a node and is recorded in L

    DFSInfo(Graph graph) {
        int n = graph.n;
        k = 0;
        T = new int[n];
        L = new int[n];
        count = n;
    }
}


// your "main program" should look something like this:

public class Solution {

    static void recDFS(int u, Graph graph, DFSInfo info) {
        // perform a recursive DFS, starting at u
        info.T[u] = info.k;
        Item node = graph.A[u];
        while (node != null){
            if (info.T[node.data] == 0){
                recDFS(node.data, graph, info);
            }
            node = node.next;
        }
        info.L[info.count - 1] = u;
        info.count -= 1;
    }

    static DFSInfo DFS(int[] order, Graph graph) {
        // performs a "full" DFS on given graph, processing
        // nodes in the order specified (i.e., order[0], order[1], ...)
        // in the main loop.
        DFSInfo info = new DFSInfo(graph);

        for (int j = 0; j < order.length; j++) {
            if(info.T[order[j]] == 0){
                info.k += 1;
                recDFS(order[j],graph,info);
            }
        }

        return info;
    }

    static boolean[] computeSafeNodes(Graph graph, DFSInfo info) {
        // returns a boolean array indicating which nodes
        // are safe nodes.  The DFSInfo is that computed from the
        // second DFS.

        int[] outDegreeList = new int[info.k];
        for (int i = 0; i < graph.n; i++) {
            Item u = graph.A[i];
            while(u != null){
                if(info.T[i] != info.T[u.data]){
                    outDegreeList[info.T[i] - 1] += 1;
                }
                u = u.next;
            }
        }

        boolean[] safeNode = new boolean[graph.n];
        for (int j = 0; j < outDegreeList.length; j++) {
            if (outDegreeList[j] == 0){
                for (int h = 0; h < safeNode.length; h++) {
                    if (info.T[h] == j + 1){
                        safeNode[h] = true;
                    }
                }
            }
        }

        return safeNode;
    }

    static Graph reverse(Graph graph) {
        // returns the reverse of the given graph
        Graph reverseGraph = new Graph(graph.n);
        for (int i = 0; i < graph.n; i++) {
            Item u = graph.A[i];
            while(u != null){
                reverseGraph.addEdge(u.data, i);
                u = u.next;
            }
        }
        return reverseGraph;
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
            graph.addEdge(u,v);
        }

        //Perform DFS on reverse Graph
        Graph reverseGraph = reverse(graph);
        int[] firstOrder = IntStream.range(0, graph.n).toArray();
        DFSInfo reverseInfo = DFS(firstOrder, reverseGraph);
        DFSInfo normalInfo = DFS(reverseInfo.L, graph);
        boolean[] safeNode = computeSafeNodes(graph, normalInfo);
        for (int j = 0; j < safeNode.length; j++) {
            if (safeNode[j] == true){
                System.out.print(Integer.toString(j) + " ");
            }
        }
    }

}