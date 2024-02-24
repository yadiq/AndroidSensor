package com.hqumath.demo.media.structBean;

/**
 * 环形队列（Ring Buffer/Circular Buffer/Cyclic Buffer ）
 * 检测缓冲区是满或是空的策略：浪费一个空间
 * 特性：可覆盖，不阻塞。数据不安全
 * https://blog.csdn.net/sking002007/article/details/6584590
 * https://hedzr.com/algorithm/golang/ringbuf-index/
 */
public class CircularCoverQueue {
    private int head = 0;//头部，出队列方向
    private int tail = 0;//尾部，入队列方向
    private int size;//队列大小
    private Object items[];//队列空间

    public CircularCoverQueue(int size) {
        this.size = size;
        items = new Object[size];
    }

    //入队
    public boolean enqueue(Object item) {
        //队列已满，覆盖
        if ((tail + 1) % size == head) {
            head = (head + 1) % size;
        }
        items[tail] = item;
        tail = (tail + 1) % size;
        return true;
    }

    //出队
    public Object dequeue() {
        //队列为空
        if (head == tail)
            return null;
        Object result = items[head];
        head = (head + 1) % size;
        return result;
    }

    public boolean isEmpty(){
        return head == tail;
    }

    //有效数据
    public int getAvailableSize() {
        return (tail - head + size) % size;
    }

    public void clear() {
        head = 0;
        tail = 0;
    }
}
