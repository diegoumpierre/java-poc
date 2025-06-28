package br.com.poc.TestMain;

import br.com.poc.pool.GenericPool;
import br.com.poc.task.GenericTask;
import br.com.poc.task.TaskDelay;
import br.com.poc.task.TaskDelayReentrantLock;

import java.util.concurrent.locks.ReentrantLock;

public class TestPool {

//    public static void main(String[] args){
//
//        GenericPool pool = new GenericPool(5,"pool-1");
//        for (int i =0;i<10;i++){
//            GenericTask task = new TaskDelay("TASK-"+100*i, 10*10);
//            pool.addTask(task);
//        }
//
//        GenericPool pool2 = new GenericPool(2,"pool-2");
//        for (int i =0;i<10;i++){
//            GenericTask task = new TaskDelay("TASK-"+500*i, 10*10);
//            pool2.addTask(task);
//        }
//    }

    public static void main(String[] args){

        ReentrantLock reentrantLock = new ReentrantLock();

        GenericPool pool = new GenericPool(5,"pool-1");
        for (int i =0;i<10;i++){
            GenericTask task = new TaskDelayReentrantLock("TASK-"+100*i, 10*10,reentrantLock);
            pool.addTask(task);
        }

        GenericPool pool2 = new GenericPool(2,"pool-2");
        for (int i =0;i<10;i++){
            GenericTask task = new TaskDelayReentrantLock("TASK-"+500*i, 10*10,reentrantLock);
            pool2.addTask(task);
        }
    }


    /*
    ``` Lock is an interface. It defines a set of methods that all locks should have.
    ReentrantLock is a concrete class that implements the Lock interface.
     It implements all the methods defined in Lock , plus much more ```


This gives a lock to the current working thread and blocks all other threads which are trying to take a lock on the
 shared resource. As the name says, ReentrantLock allows threads to enter into the lock on a resource more than once.
 When the thread first enters into the lock, a hold count is set to one.

What is the difference between ReentrantLock and synchronized?
Image result for ReentrantLock
As stated earlier, the main difference between synchronized and ReentrantLock is the ability to trying to
lock interruptibly, and with a timeout. The thread doesn't need to block infinitely, which was the case
with synchronized.

     */


}