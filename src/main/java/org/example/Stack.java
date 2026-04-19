package org.example;

public class Stack<T> {
    public Node<T> top;
    public Stack(){
        this.top = null;
    }
    public void Push(T data){
        Node<T> newNode = new Node<>(data);
        newNode.next = this.top;
        this.top = newNode;
    }
    public T Pop(){
        T data = this.top.data;
        this.top = this.top.next;
        return data;
    }
    public boolean isEmpty(){
        return this.top == null;
    }
    public int size(){
        if(this.isEmpty()) return 0;
        Node<T> temp = this.top;
        int count = 0;
        while(temp != null){
            count++;
            temp = temp.next;
        }
        return count;
    }
    public T peek(){
        return this.top.data;
    }
}
