package com.jmcoin.network;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ReceiverThread<X extends TemplateThread> implements Runnable{

    protected ObjectInputStream input;
    protected X runnable; 

    public ReceiverThread(X workerRunnable) {
        runnable = workerRunnable;
        input = workerRunnable.getIn();
    }

    @Override
    public void run() {
        boolean loop = true;
        try {
            do {
                Object read = input.readObject();
                if (read != null) {
                    System.out.println("Receiver Thread "+" #"+Thread.currentThread().getId() +" "+ this.runnable.getProtocol().getClass().getSimpleName()+" read : " + read.toString());
                    System.out.println("Class: "+this.runnable.getClass().getSimpleName());
                    this.runnable.handleMessage(read);
                }
                Thread.sleep(10);
            }while (loop);
        } catch (IOException e) {
            try {
                this.runnable.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException|InterruptedException e) {
            e.printStackTrace();
        }

    }
}
