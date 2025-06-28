package br.com.poc.TestMain;

import br.com.poc.atomic.pool.GenericPool;
import br.com.poc.task.GenericTask;
import br.com.poc.task.TaskDelayJustEnd;

import static java.lang.Thread.sleep;

public class TestPool {

    public static void main(String[] args){

//        //Proove the difference between atomic and no atomic
//        br.com.poc.atomic.pool.GenericPool pool = new GenericPool(2000,"pool-1");
//        for (int i =0;i<100;i++){
//            GenericTask task = new TaskDelayJustEnd("TASK-"+i, 10*100);
//            pool.addTask(task);
//        }
//        try {
//            sleep(200);
//        } catch (Exception error) {
//            System.out.println(error.getMessage());
//        }
//        pool.stopAllThreads();
//
//        try {
//            sleep(400);
//        } catch (Exception error) {
//            System.out.println(error.getMessage());
//        }
//
//        pool.printAll();
//        pool.getPrintList();

        //Proove the difference between atomic and no atomic
        br.com.poc.synchronized1.pool.GenericPool pool = new br.com.poc.synchronized1.pool.GenericPool(2000,"pool-1");
        for (int i =0;i<100;i++){
            GenericTask task = new TaskDelayJustEnd("TASK-"+i, 10*100);
            pool.addTask(task);
        }
        try {
            sleep(200);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
        pool.stopAllThreads();

        try {
            sleep(400);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }

        pool.printAll();
        pool.getPrintList();


    }
}