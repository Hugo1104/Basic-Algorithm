import java.io.*;
import java.util.*;


class Node {
    String guide;
    int value;
}

class InternalNode extends Node {
    Node child0, child1, child2;
}

class LeafNode extends Node {
}


class TwoThreeTree {
    Node root;
    int height;

    TwoThreeTree() {
        root = null;
        height = -1;
    }
}

class WorkSpace {
// this class is used to hold return values for the recursive doInsert
// routine (see below)

    Node newNode;
    int offset;
    boolean guideChanged;
    Node[] scratch;
}

public class Solution {

    public static void main(String[] args) throws IOException {
        //create a 2-3 tree
        TwoThreeTree tree = new TwoThreeTree();

        //reading file input and insert the node
        Scanner scanner = new Scanner(System.in);
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out,"ASCII"), 4096);
        int queryNum = Integer.parseInt(scanner.next());
        for (int i = 0; i < queryNum; i++) {
            int typeNum = Integer.parseInt(scanner.next());
            if (typeNum == 1){
                String guide = scanner.next();
                int value = Integer.parseInt(scanner.next());
                insert(guide, value, tree);
            }else if(typeNum == 2){
                String x = scanner.next();
                String y = scanner.next();
                int delta = Integer.parseInt(scanner.next());
                if (tree.root != null) {
                    if(x.compareTo(y) <= 0) {
                        addRange(x, y, delta, tree);
                    }else{
                        addRange(y, x, delta, tree);
                    }
                }
            }else{
                String a = scanner.next();
                Node[] b = new Node[tree.height + 1];
                int effective = search(a, tree.root, tree.height, b);
                if (a.compareTo(b[tree.height].guide) == 0){
                    output.write(effective + "\n");
                }else{
                    output.write("-1\n");
                }
            }

        }
        output.flush();



    }

    static void insert(String key, int value, TwoThreeTree tree) {
        // insert a key value pair into tree (overwrite existing value
        // if key is already present)

        int h = tree.height;

        if (h == -1) {
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key;
            newLeaf.value = value;
            tree.root = newLeaf;
            tree.height = 0;
        }
        else {
            WorkSpace ws = doInsert(key, value, tree.root, h);

            if (ws != null && ws.newNode != null) {
                // create a new root

                InternalNode newRoot = new InternalNode();
                if (ws.offset == 0) {
                    newRoot.child0 = ws.newNode;
                    newRoot.child1 = tree.root;
                }
                else {
                    newRoot.child0 = tree.root;
                    newRoot.child1 = ws.newNode;
                }
                resetGuide(newRoot);
                tree.root = newRoot;
                tree.height = h+1;
            }
        }
    }

    static WorkSpace doInsert(String key, int value, Node p, int h) {
        // auxiliary recursive routine for insert

        if (h == 0) {
            // we're at the leaf level, so compare and
            // either update value or insert new leaf

            LeafNode leaf = (LeafNode) p; //downcast
            int cmp = key.compareTo(leaf.guide);

            if (cmp == 0) {
                leaf.value = value;
                return null;
            }

            // create new leaf node and insert into tree
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key;
            newLeaf.value = value;

            int offset = (cmp < 0) ? 0 : 1;
            // offset == 0 => newLeaf inserted as left sibling
            // offset == 1 => newLeaf inserted as right sibling

            WorkSpace ws = new WorkSpace();
            ws.newNode = newLeaf;
            ws.offset = offset;
            ws.scratch = new Node[4];

            return ws;
        }
        else {
            InternalNode q = (InternalNode) p; // downcast
            q.child0.value += q.value;
            q.child1.value += q.value;
            if (q.child2 != null){
                q.child2.value += q.value;
            }
            q.value = 0;
            int pos;
            WorkSpace ws;

            if (key.compareTo(q.child0.guide) <= 0) {
                pos = 0;
                ws = doInsert(key, value, q.child0, h-1);
            }
            else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
                pos = 1;
                ws = doInsert(key, value, q.child1, h-1);
            }
            else {
                pos = 2;
                ws = doInsert(key, value, q.child2, h-1);
            }

            if (ws != null) {
                if (ws.newNode != null) {
                    // make ws.newNode child # pos + ws.offset of q

                    int sz = copyOutChildren(q, ws.scratch);
                    insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
                    if (sz == 2) {
                        ws.newNode = null;
                        ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
                    }
                    else {
                        ws.newNode = new InternalNode();
                        ws.offset = 1;
                        resetChildren(q, ws.scratch, 0, 2);
                        resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
                    }
                }
                else if (ws.guideChanged) {
                    ws.guideChanged = resetGuide(q);
                }
            }

            return ws;
        }
    }


    static int copyOutChildren(InternalNode q, Node[] x) {
        // copy children of q into x, and return # of children

        int sz = 2;
        x[0] = q.child0; x[1] = q.child1;
        if (q.child2 != null) {
            x[2] = q.child2;
            sz = 3;
        }
        return sz;
    }

    static void insertNode(Node[] x, Node p, int sz, int pos) {
        // insert p in x[0..sz) at position pos,
        // moving existing extries to the right

        for (int i = sz; i > pos; i--)
            x[i] = x[i-1];

        x[pos] = p;
    }

    static boolean resetGuide(InternalNode q) {
        // reset q.guide, and return true if it changes.

        String oldGuide = q.guide;
        if (q.child2 != null)
            q.guide = q.child2.guide;
        else
            q.guide = q.child1.guide;

        return q.guide != oldGuide;
    }


    static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
        // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
        // also resets guide, and returns the result of that

        q.child0 = x[pos];
        q.child1 = x[pos+1];

        if (sz == 3)
            q.child2 = x[pos+2];
        else
            q.child2 = null;

        return resetGuide(q);
    }


    /**
     * add the value to the node value
     * @param p
     * @param h
     */
    static void addAll(Node p, int h){
        p.value = p.value + h;
    }

    /**
     * find where x will fit in in the tree regardless if there is an equal value in the tree
     * @param x key to search in the tree
     * @param p root of the tree
     * @param h height of the tree
     * @param path array that store the search path
     */
    static int search (String x, Node p, int h, Node[] path){

        int effective = 0;

        if (h == 0){
            path[h] = p;
            return p.value;
        }else {
            InternalNode searchNode = (InternalNode) p;

            for (int i = 0; i < h - 1; i++) {
                if (x.compareTo(searchNode.child0.guide) <= 0) {
                    path[i] = searchNode;
                    effective += searchNode.value;
                    searchNode = (InternalNode) searchNode.child0;
                } else if (searchNode.child2 == null || x.compareTo(searchNode.child1.guide) <= 0) {
                    path[i] = searchNode;
                    effective += searchNode.value;
                    searchNode = (InternalNode) searchNode.child1;
                } else {
                    path[i] = searchNode;
                    effective += searchNode.value;
                    searchNode = (InternalNode) searchNode.child2;
                }
            }

            if (x.compareTo(searchNode.child0.guide) <= 0) {
                path[h - 1] = searchNode;
                effective += searchNode.value;
                LeafNode leaf = (LeafNode) searchNode.child0;
                path[h] = leaf;
                effective += leaf.value;
            } else if (searchNode.child2 == null || x.compareTo(searchNode.child1.guide) <= 0) {
                path[h - 1] = searchNode;
                effective += searchNode.value;
                LeafNode leaf = (LeafNode) searchNode.child1;
                path[h] = leaf;
                effective += leaf.value;
            } else {
                path[h - 1] = searchNode;
                effective += searchNode.value;
                LeafNode leaf = (LeafNode) searchNode.child2;
                path[h] = leaf;
                effective += leaf.value;
            }

            return effective;
        }
    }


    static void addRange (String x, String y, int delta, TwoThreeTree tree){

        //get the search path for x, y
        Node[] pathx = new Node[tree.height + 1];
        Node[] pathy = new Node[tree.height + 1];
        search(x, tree.root, tree.height, pathx);
        search(y, tree.root, tree.height, pathy);

        //calculate the divergence point
        int divergenceIndex = 0;
        for (int g = 0; g < pathx.length; g++) {
            if (pathx[g].guide.equals(pathy[g].guide)){
                divergenceIndex = g;
            }else{
                break;
            }
        }

        //if x, y path do not diverge
        if (divergenceIndex == tree.height){
            LeafNode finalLeaf = (LeafNode) pathx[tree.height];
            if (finalLeaf.guide.compareTo(x) >= 0 && finalLeaf.guide.compareTo(y) <= 0) {
                addAll(finalLeaf, delta);
            }
            return;
        }

        //determine if leaf node for x will increase
        LeafNode leafNodeX = (LeafNode) pathx[tree.height];
        String xleafGuide = leafNodeX.guide;

        //if leaf node is in range
        if (x.compareTo(xleafGuide) <= 0){
            addAll(leafNodeX, delta);
        }

        //walk the search path from x to divergence point
        for (int h = tree.height; h > divergenceIndex + 1; h--) {
            String guide = pathx[h].guide;
            InternalNode previousNode = (InternalNode) pathx[h-1];

            if (previousNode.child2 == null){//two nodes
                if (guide.compareTo(previousNode.guide) < 0){//this node is the right node
                    addAll(previousNode.child1, delta);
                }
            }else{//three nodes
                if (guide.compareTo(previousNode.child1.guide) < 0){//this is the right node
                    addAll(previousNode.child1, delta);
                    addAll(previousNode.child2, delta);
                }else if (guide.compareTo(previousNode.child1.guide) == 0){//this is the middle node
                    addAll(previousNode.child2, delta);
                }
            }
        }

        //process the divergence point where divergence point has three nodes
        InternalNode diverNode = (InternalNode) pathx[divergenceIndex];
        if (diverNode.child2 != null){//three nodes
            if (diverNode.child0.guide.compareTo(pathx[divergenceIndex + 1].guide) == 0 &&
                    diverNode.child2.guide.compareTo(pathy[divergenceIndex + 1].guide) == 0 ){
                //x comes from left-est node and y comes from right-est node
                addAll(diverNode.child1, delta);
            }
        }

        //walk the search path from y to divergence point
        for (int f = divergenceIndex + 2; f <= tree.height; f++) {
            String guide = pathy[f].guide;
            InternalNode previousNode = (InternalNode) pathy[f-1];

            if (previousNode.child2 == null){//two nodes
                if (guide.compareTo(previousNode.guide) == 0){//this node is the left node
                    addAll(previousNode.child0, delta);
                }
            }else{//three nodes
                if (guide.compareTo(previousNode.child1.guide) > 0){//this is the left node
                    addAll(previousNode.child0, delta);
                    addAll(previousNode.child1, delta);
                }else if (guide.compareTo(previousNode.child1.guide) == 0){//this is the middle node
                    addAll(previousNode.child0, delta);
                }
            }
        }

        //determine if leaf node for y will be printed out
        LeafNode leafNodeY = (LeafNode) pathy[tree.height];
        String yleafGuide = leafNodeY.guide;

        //if leaf node is in range
        if (y.compareTo(yleafGuide) >= 0){
            addAll(leafNodeY, delta);
        }
    }
}
