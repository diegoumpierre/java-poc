package br.com.poc.TestMain;

import br.com.poc.pool.GenericPool;
import br.com.poc.task.GenericTask;
import br.com.poc.task.TaskDelayJustEnd;
import br.com.poc.task.TaskException;

public class TestPool {

    public static void main(String[] args){

        GenericPool pool = new GenericPool(2,"pool-1");
        for (int i =0;i<100;i++){
            GenericTask task = new TaskDelayJustEnd("TASK-"+i, 10*100);
            pool.addTask(task);
        }

        pool.stopAllThreads();

        GenericPool pool2 = new GenericPool(2,"pool-2");
        for (int i =0;i<10;i++){
            GenericTask task = new TaskDelayJustEnd("TASK-"+i, 10*100);
            pool2.addTask(task);
        }

        pool.resizePool(100);

        GenericPool pool3 = new GenericPool(2,"pool-3");
        for (int i =0;i<1;i++){
            GenericTask task = new TaskException("TASK-"+i, 10*100);
            pool3.addTask(task);
        }
    }
}