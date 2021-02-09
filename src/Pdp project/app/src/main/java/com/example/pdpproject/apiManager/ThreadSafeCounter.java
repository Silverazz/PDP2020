package com.example.pdpproject.apiManager;


public class ThreadSafeCounter {
    private int counter;

    public ThreadSafeCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public synchronized void PCounter() {
        this.counter --;
    }
}
