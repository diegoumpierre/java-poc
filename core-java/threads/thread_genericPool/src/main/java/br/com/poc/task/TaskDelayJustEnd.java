package br.com.poc.task;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDelayJustEnd implements GenericTask {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");
    private final String END =   "TASK = {2} | END   - {0} | DELAY = {1} | Thread: {3}";

    private String taskName;
    private long delayMiliseconds;

    public TaskDelayJustEnd(String taskName, long delayMiliseconds){
        this.taskName = taskName;
        this.delayMiliseconds = delayMiliseconds;
    }


    @Override
    public void run(String threadName) {
        try {
            Thread.sleep(delayMiliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(
                MessageFormat.format(
                        END,
                        simpleDateFormat.format(new Date()),
                        delayMiliseconds,
                        taskName,
                        threadName));
    }
    @Override
    public String getTaskName() {
        return this.taskName;
    }
}