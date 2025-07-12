package br.com.poc.pool;

import br.com.poc.task.GenericTask;
import br.com.poc.thread.GenericThread;
import lombok.Data;
import org.joda.time.DateTime;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class GenericPool {

    private String poolName;
    private int poolMaxThreads = 0;
    private int poolThreadsActive = 0;

    private int qtdTaskAdd = 0;
    private int qtdTaskFinished = 0;


    private DateTime startTimeoutDate = null;
    private int poolTimeOutMin = 1;

    boolean isStarted = false;

    final String START = "== BLOCKING => START - {0} Threads: {1}";

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");
    final String END = "== BLOCKING => END - {0} Threads: {1}";

    private BlockingQueue<GenericTask> genericTaskBlockingQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<GenericTask> genericTaskBlockingQueueFinish = new LinkedBlockingQueue<>();

    public boolean allTasksAreComplete(){
        return qtdTaskAdd == qtdTaskFinished;
    }

    public void setTaskFinished(){
        qtdTaskFinished++;
    }

    public GenericTask giveMeTask(){

        if ( genericTaskBlockingQueue.size() > 1 && !isStarted){
//            System.out.println(MessageFormat.format(START,simpleDateFormat.format(new Date())));
            isStarted = true;
        }

        GenericTask nextTask = genericTaskBlockingQueue.poll();

        if ( genericTaskBlockingQueue.size() < 1 && isStarted){
//            System.out.println(MessageFormat.format(END,simpleDateFormat.format(new Date())));
            isStarted = false;
        }
        return nextTask;
   }

    public synchronized boolean stoppingThread() {

        if (startTimeoutDate != null && DateTime.now().minusMinutes(poolTimeOutMin).isAfter(startTimeoutDate)){
            poolMaxThreads = 0;
        }

        if (poolMaxThreads == 0) {
            return true;
        } else if (poolThreadsActive > poolMaxThreads) {
            poolThreadsActive--;
            return true;
        }
        return false;
    }

    public GenericPool(int maxThreads,String poolName){
        this.poolMaxThreads = maxThreads;
        this.poolName = poolName;

        for(int i=0;i<maxThreads;i++){
            createThread(i);
        }
    }

    public void addTask(GenericTask task){
        genericTaskBlockingQueue.add(task);
        qtdTaskAdd++;
    }


    private void createThread(int numberThread){
        GenericThread genericThread = new GenericThread();
        genericThread.setName(poolName +"| Thread-"+numberThread);
        genericThread.setPool(this);
        genericThread.start();
        this.poolThreadsActive++;
    }

    public void resizePool(int maxThreads){
        poolMaxThreads = maxThreads;
        for(int i = poolThreadsActive;i < maxThreads;i++){
            createThread(i);
        }
    }

    public void stopAllThreads(){
        poolMaxThreads = 0;
    }

    public void setPoolTimeOutMin(int min){
        this.poolTimeOutMin = min;
    }
    public String getPoolName(){return this.poolName;}
}
