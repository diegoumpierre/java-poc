package br.com.poc.task;

public interface GenericTask {
    void run(String threadName);

    String getTaskName();
}
