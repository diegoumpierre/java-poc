package br.com.poc.pool;

import br.com.poc.task.GenericTask;
import br.com.poc.thread.GenericThread;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.DateTimeException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class GenericPool {

    private String poolName;
    private int poolMaxThreads = 0;
    private int poolThreadsActive = 0;

    private DateTime startTimeoutDate = null;
    private int poolTimeOutMin = 1;

    private BlockingQueue<GenericTask> genericTaskBlockingQueue = new LinkedBlockingQueue<>();
    //ConcurrentLinkedQueue - non block
    //ver a diferença de peformance
    // tentar com volumes
    //mete milhares para comparações
    //como funciona a solução no block


    public GenericTask giveMeTask(){
        return genericTaskBlockingQueue.poll();

//        if (!genericTaskBlockingQueue.isEmpty()){
//            startTimeoutDate = null;
//            GenericTask genericTask = null;
//            try {
//                genericTask = genericTaskBlockingQueue.take();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            //genericTaskBlockingQueue.remove(genericTask);
//            return genericTask;
//        }else{
//            if (startTimeoutDate == null)
//                startTimeoutDate = new DateTime();
//        }
//        return null;
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
