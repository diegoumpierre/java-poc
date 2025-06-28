package br.com.poc.thread;

import br.com.poc.pool.GenericPool;
import br.com.poc.pool.GenericPool2;
import br.com.poc.task.GenericTask;

import java.text.MessageFormat;


public class GenericThread2 extends Thread {

    private final String TASK_ERROR = "TASK ERROR !!! | POOL - {0} | TASK = {1} | ERROR: {2}";

    private GenericPool2 pool2 = null;


    public void setPool(GenericPool2 pool) {
        this.pool2 = pool;
    }

    public void run() {
        while (!currentThread().isInterrupted()) {
            if (pool2.stoppingThread()){
                this.interrupt();
            }else {
                GenericTask task = pool2.giveMeTask();
                if (task != null) {
                    try {
                        task.run(currentThread().getName());
                    } catch (Exception error) {
                        System.out.println(MessageFormat.format(TASK_ERROR, pool2.getPoolName(), task.getTaskName(), error.getMessage()));
                    }
                    pool2.setTaskFinished();
                }
            }
        }
    }
}
