package com.xx.juc.pc;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * 消息队列类
 */
@Slf4j
public class MessageQueue {
    // 消息队列
    private LinkedList<Message> queue = new LinkedList<>();
    // 容量大小
    private int capcity;

    public MessageQueue(int capcity){
        this.capcity = capcity;
    }
    public Message take(){
        synchronized (queue){
            while(queue.isEmpty()){
                try {
                    log.debug("队列为空的");
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Message message = queue.removeFirst();
            log.debug("获取message -> " + message.getId() + ", " + message.getValue());
            queue.notifyAll();
            return message;
        }
    }

    public void put(Message message){
        synchronized (queue){
            while(queue.size() == capcity){
                try {
                    log.debug("队列已满");
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            queue.addLast(message);
            log.debug("推送message -> " + message.getId() + ", " + message.getValue());
            queue.notifyAll();
        }

    }

    public int getCapcity() {
        return capcity;
    }
}

final class Message{
    private int id;
    private Object value;

    public Message(int id,Object value){
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }
}
