
import java.util.*;

public class LinkedList<E> {

    //Implementing doubly link list node
    private Node head;
    private Node tail;
    private int size;
    private class Node {

        E element;
        Node next;
        Node prev;

        public Node(E element, Node next, Node prev) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }
    }

    //size of doubly linked list
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    
    //method to add object to first node
    public void addFirst(E element) {
        Node temp = new Node(element, head, null);
        if (head != null) {
            head.prev = temp;
        }
        head = temp;
        if (tail == null) {
            tail = temp;
        }
        size++;
    }

    //method to add object to last node
    public void addLast(E element) {
        Node temp = new Node(element, null, tail);
        if (tail != null) {
            tail.next = temp;
        }
        if (head == null) {
            head = temp;
        }
        size++;
    }

    //method to move to next node
    public void next() {
        Node temp = tail;
        while (temp != null) {
            System.out.println(temp.element);
            temp = temp.prev;
        }
    }

    //method to move to previous node
    public void previous() {
        Node temp = tail;
        while (temp != null) {
            System.out.println(temp.element);
            temp = temp.prev;
        }
    }

    //method to remove last node
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        Node temp = tail;
        tail = tail.prev;
        tail.next = null;
        size--;
        return temp.element;
    }
    
}
