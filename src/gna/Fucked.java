package gna;

import java.util.Comparator;
import java.util.PriorityQueue;

class Node {
    int value;

    public Node(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }
}

public class Fucked {

    public static void main(String[] args) {

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getValue));

        Node a = new Node(10);
        Node b = new Node(6);

        queue.add(a);
        queue.add(b);

        a.value = 4;

        System.out.println(queue);


    }


}
