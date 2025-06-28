package br.com.poc.atomic.pool;

import br.com.poc.LogSaida;
import br.com.poc.atomic.Counter;
import br.com.poc.atomic.CounterAtomic;
import br.com.poc.atomic.thread.GenericThread;
import br.com.poc.task.GenericTask;
import org.joda.time.DateTime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class GenericPool {

    private String poolName;
    private int poolMaxThreads = 0;
    private int poolThreadsActive = 0;

    private DateTime startTimeoutDate = null;
    private int poolTimeOutMin = 1;

    public Counter counter = new Counter();
    public CounterAtomic counterAtomic = new CounterAtomic();

    private BlockingQueue<LogSaida> printList = new LinkedBlockingQueue<>();

    public void getPrintList() {

        int maxForNoAtomic = 0;
        int maxForAtomic = 0;

        for (LogSaida logSaida :printList){

            if (logSaida.isAtomic()){
                if (logSaida.getCounter() > maxForAtomic){
                    maxForAtomic = logSaida.getCounter();
                }
            }else{
                if (logSaida.getCounter() > maxForNoAtomic){
                    maxForNoAtomic = logSaida.getCounter();
                }
            }
        }

        System.out.println("Max Atomic -->"+maxForAtomic);
        System.out.println("Max no Atomic -->"+maxForNoAtomic);

    }

    public void printAll(){
        for (LogSaida logSaida :printList){
            logSaida.printAll();
        }
    }



    public void addPrintList(String threadName,Integer counter,Boolean isAtomic) {
        printList.add(LogSaida.builder()
                .threadName(threadName)
                .counter(counter)
                .atomic(isAtomic)
                .build());

    }

    private BlockingQueue<GenericTask> genericTaskBlockingQueue = new LinkedBlockingQueue<>();
    //ConcurrentLinkedQueue - non block

    public GenericTask giveMeTask(){
        genericTaskBlockingQueue.poll();

        if (!genericTaskBlockingQueue.isEmpty()){
            startTimeoutDate = null;
            GenericTask genericTask = null;
            try {
                genericTask = genericTaskBlockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            genericTaskBlockingQueue.remove(genericTask);
            return genericTask;
        }else{
            if (startTimeoutDate == null)
                startTimeoutDate = new DateTime();
        }
        return null;
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
