import java.io.*;
import java.util.*;

//person class
class Person{
    String name;
    long score;
    int pos;
}

class MinHeap{
    Person[] heap;
    int size;
    int maxsize;

    public MinHeap(int maxsize){
        this.maxsize = maxsize;
        this.size = 0;
        heap = new Person[maxsize];
    }

    int parent(int pos){
        if (pos <= 0) return -1;
        return (pos - 1) / 2;
    }

    private int leftChild(int pos){
        if (pos >= size / 2) return -1;
        return 2 * pos + 1;
    }

    private int rightChild(int pos){
        if (pos >= (size - 1) / 2) return -1;
        return 2 * pos + 2;
    }

    private boolean isLeaf (int pos){
        if (pos >= (size / 2) && pos < size) {
            return true;
        }
        return false;
    }

    void swap(int firstPos, int secPos){
        Person temp;
        temp = heap[firstPos];
        heap[firstPos] = heap[secPos];
        heap[firstPos].pos = firstPos;
        heap[secPos] = temp;
        heap[secPos].pos = secPos;
    }

    void minHeapify(int pos){
        while (!isLeaf(pos)) {
            int leftPos = leftChild(pos);
            if ((leftPos < (size - 1)) && (heap[leftPos].score > heap[leftPos + 1].score)){
                leftPos++; //leftPos is now the smaller element
            }
            if (heap[pos].score <= heap[leftPos].score) return;
            swap(pos, leftPos);
            pos = leftPos;
        }
    }

    void insert(Person element){
        if (size >= maxsize) {
            return;
        }

        int current = size++;
        heap[current] = element;

        while (current!= 0 && (heap[current].score < heap[parent(current)].score)) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    void removeMin(){
        if (size == 0) return;
        swap(0, size - 1);
        heap[size - 1] = null;
        size--;
        minHeapify(0);
    }
}

public class Solution {

    public static void main(String[] args){
        //read file input and insert into the heap
        Scanner scanner = new Scanner(System.in);
        int heap_size = Integer.parseInt(scanner.next());

        //create a minHeap and a hashtable
        MinHeap minHeap = new MinHeap(heap_size);
        Hashtable hashtable = new Hashtable(heap_size);
        for (int i = 0; i < heap_size; i++) {
            Person newPerson = new Person();
            newPerson.name = scanner.next();
            newPerson.score = Long.parseLong(scanner.next());
            newPerson.pos = i;

            //store the element into minHeap and hashtable
            minHeap.insert(newPerson);
            hashtable.put(newPerson.name, newPerson);
        }

        int commandNum = Integer.parseInt(scanner.next());
        for (int j = 0; j < commandNum; j++) {
            int commandType = Integer.parseInt(scanner.next());
            if (commandType == 1){
                String updateName = scanner.next();
                long updateScore = Long.parseLong(scanner.next());
                Person updatePerson = (Person) hashtable.get(updateName);
                int personPos = updatePerson.pos;
                //update the score of person
                updatePerson.score += updateScore;

                //update pos in the heap
                minHeap.minHeapify(personPos);
            }else{
                long criteria = Long.parseLong(scanner.next());
                while (minHeap.heap[0].score < criteria){
                    minHeap.removeMin();
                }
                System.out.println(minHeap.size);

            }
        }

/*        Enumeration names = hashtable.keys();

        while(names.hasMoreElements()){
            String str = (String) names.nextElement();
            Person stored = (Person) hashtable.get(str);
            System.out.println(str + ":" + stored.score + " " + stored.pos);
        }

        System.out.println();

        for (int h = 0; h < minHeap.size; h++) {
            System.out.println(minHeap.heap[h].name + ": " + minHeap.heap[h].score + " " + minHeap.heap[h].pos);
        }*/
    }

}
