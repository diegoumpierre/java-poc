package br.com.poc.pool;

import br.com.poc.task.GenericTask;
import br.com.poc.thread.GenericThread2;
import org.joda.time.DateTime;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;


public class GenericPool2 {

    private String poolName;
    private int poolMaxThreads = 0;
    private int poolThreadsActive = 0;

    private DateTime startTimeoutDate = null;
    private int poolTimeOutMin = 1;
    private int qtdTaskAdd = 0;
    private int qtdTaskFinished = 0;


    boolean isStarted = false;

    final String START = "== NO BLOCKING => START - {0} Threads: {1}";

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm : ss : SSS");
    final String END = "== NO BLOCKING => END - {0} Threads: {1}";

    private ConcurrentLinkedQueue<GenericTask> genericTaskConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    //ConcurrentLinkedQueue - non block
    //ver a diferença de peformance
    // tentar com volumes
    //mete milhares para comparações
    //como funciona a solução no block

    public boolean allTasksAreComplete(){
        return qtdTaskAdd == qtdTaskFinished;
    }

    public void setTaskFinished(){
        qtdTaskFinished++;
    }
    public GenericTask giveMeTask(){

        if ( genericTaskConcurrentLinkedQueue.size() > 1 && !isStarted){
//            System.out.println(MessageFormat.format(START,simpleDateFormat.format(new Date())));
            isStarted = true;
        }

        GenericTask nextTask = genericTaskConcurrentLinkedQueue.poll();

        if ( genericTaskConcurrentLinkedQueue.size() < 1 && isStarted){
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

    public GenericPool2(int maxThreads, String poolName){
        this.poolMaxThreads = maxThreads;
        this.poolName = poolName;

        for(int i=0;i<maxThreads;i++){
            createThread(i);
        }
    }

    public void addTask(GenericTask task){

        genericTaskConcurrentLinkedQueue.add(task);
        qtdTaskAdd++;
    }

    private void createThread(int numberThread){
        GenericThread2 genericThread = new GenericThread2();
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
