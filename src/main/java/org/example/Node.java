package org.example;

public class Node<T> {
    public T data;
    public Node<T> next;
    public Node(T data){
        this.data = data;
        this.next = null;
    }
    public Node(){
        this.data = null;
        this.next = null;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return this.next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void Push(T data){
        Node<T> newNode = new Node<>(data);
        newNode.next = this.next;
        this.next = newNode;
    }

    public T Pop(){
        T data = this.data;
        this.data = this.next.data;
        this.next = this.next.next;
        return data;
    }

    public boolean isEmpty(){
        return this.next == null;
    }


}
