package br.com.poc.task;

import java.text.SimpleDateFormat;

public class TaskDelay implements GenericTask {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");

    private final String START = "TASK = {2} | START - {0} | DELAY = {1} | Thread: {3}";
    private final String END =   "TASK = {2} | END   - {0} | DELAY = {1} | Thread: {3}";

    private String taskName;
    private long delayMiliseconds;

    boolean print = false;

    public TaskDelay(String taskName, long delayMiliseconds){
        this.taskName = taskName;
        this.delayMiliseconds = delayMiliseconds;
    }


    @Override
    public void run(String threadName) {
        if (print) {
//            System.out.println(
//                    MessageFormat.format(
//                            START,
//                            simpleDateFormat.format(new Date()),
//                            delayMiliseconds,
//                            taskName,
//                            threadName));
        }
        try {
            Thread.sleep(delayMiliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (print) {
//            System.out.println(
//                    MessageFormat.format(
//                            END,
//                            simpleDateFormat.format(new Date()),
//                            delayMiliseconds,
//                            taskName,
//                            threadName));
        }
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }
}