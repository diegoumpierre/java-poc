package br.com.poc.atomic.thread;

import br.com.poc.atomic.pool.GenericPool;
import br.com.poc.task.GenericTask;

import java.text.MessageFormat;


public class GenericThread extends Thread {

    private final String TASK_ERROR = "TASK ERROR !!! | POOL - {0} | TASK = {1} | ERROR: {2}";

    private GenericPool pool = null;


    public void setPool(GenericPool pool) {
        this.pool = pool;
    }


    private void printCounter() {

        try {
            sleep(200);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
        pool.addPrintList(this.getName(),pool.counter.getValue(),false);
        pool.addPrintList(this.getName(),pool.counterAtomic.getValue2(),true);

        pool.counter.increment();
        pool.counterAtomic.increment2();
    }


    public void run() {
        while (!currentThread().isInterrupted()) {
            if (pool.stoppingThread()){
                this.interrupt();
            }else {
                printCounter();

                GenericTask task = pool.giveMeTask();
                if (task != null) {
                    try {
                        task.run(currentThread().getName());
                    } catch (Exception error) {
                        System.out.println(MessageFormat.format(TASK_ERROR, pool.getPoolName(), task.getTaskName(), error.getMessage()));
                    }
                }
            }
        }
    }
}
