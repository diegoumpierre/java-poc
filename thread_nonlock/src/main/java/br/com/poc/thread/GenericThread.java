package br.com.poc.thread;

import br.com.poc.pool.GenericPool;
import br.com.poc.task.GenericTask;

import java.text.MessageFormat;


public class GenericThread extends Thread {

    private final String TASK_ERROR = "TASK ERROR !!! | POOL - {0} | TASK = {1} | ERROR: {2}";

    private GenericPool pool = null;


    public void setPool(GenericPool pool) {
        this.pool = pool;
    }

    public void run() {
        while (!currentThread().isInterrupted()) {
            if (pool.stoppingThread()){
                this.interrupt();
            }else {
                GenericTask task = pool.giveMeTask();
                if (task != null) {
                    try {
                        task.run(currentThread().getName());
                    } catch (Exception error) {
                        System.out.println(MessageFormat.format(TASK_ERROR, pool.getPoolName(), task.getTaskName(), error.getMessage()));
                    }
                    pool.setTaskFinished();
                }
            }
        }
    }
}
