package br.com.poc.task;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class TaskDelayReentrantLock implements GenericTask {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");

    private final String START = "TASK = {2} | START - {0} | DELAY = {1} | Thread: {3}";
    private final String END =   "TASK = {2} | END   - {0} | DELAY = {1} | Thread: {3}";

    private String taskName;
    private long delayMiliseconds;

    private ReentrantLock reentrantLock;

    public TaskDelayReentrantLock(String taskName
            , long delayMiliseconds
            , ReentrantLock reentrantLock){
        this.taskName = taskName;
        this.delayMiliseconds = delayMiliseconds;
        this.reentrantLock = reentrantLock;
    }


    @Override
    public void run(String threadName) {

        try {
            Thread.sleep(delayMiliseconds); //doing anything

            if (reentrantLock != null){
                msgStart(threadName);
                //Getting Outer Lock
                boolean lock = reentrantLock.tryLock();


                // Returns True if lock is free
                if(lock){
                    // Getting Inner Lock
                    reentrantLock.lock();

                    try
                    {
                        someInternalJob(threadName);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        //Inner lock release
                        System.out.println("task name - " + threadName +
                                " releasing inner lock");

                        reentrantLock.unlock();
                    }
                    System.out.println("Lock Hold Count - " + reentrantLock.getHoldCount());
                    System.out.println("task name - " + threadName + " work done");
                }else{
                    System.out.println("task name - " + threadName +
                            " waiting for lock");
                    Thread.sleep(1000);
                }
            }






        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        msgEnd(threadName);
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }


    private void msgStart(String threadName){
        System.out.println(
                MessageFormat.format(
                        START,
                        simpleDateFormat.format(new Date()),
                        delayMiliseconds,
                        taskName,
                        threadName));
    }
    private void msgEnd(String threadName){
        System.out.println(
                MessageFormat.format(
                        END,
                        simpleDateFormat.format(new Date()),
                        delayMiliseconds,
                        taskName,
                        threadName));
    }

    private void someInternalJob(String threadName) throws InterruptedException {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
        System.out.println("task name - "+ threadName
                + " inner lock acquired at "
                + ft.format(d)
                + " Doing inner work");
        System.out.println("Lock Hold Count - "+ reentrantLock.getHoldCount());

        Thread.sleep(1500);
    }


}