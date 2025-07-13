package br.com.poc.task;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskException implements GenericTask {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");
    private final String START = "TASK = {2} | START - {0} | DELAY = {1} | Thread: {3}";

    private String taskName;
    private long delayMiliseconds;

    public TaskException(String taskName, long delayMiliseconds){
        this.taskName = taskName;
        this.delayMiliseconds = delayMiliseconds;
    }


    @Override
    public void run(String threadName) {

        System.out.println(
                MessageFormat.format(
                        START,
                        simpleDateFormat.format(new Date()),
                        delayMiliseconds,
                        taskName,
                        threadName));
        try {
            Thread.sleep(delayMiliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("TASK SOME ERROR GENERIC");
    }
    @Override
    public String getTaskName() {
        return this.taskName;
    }
}