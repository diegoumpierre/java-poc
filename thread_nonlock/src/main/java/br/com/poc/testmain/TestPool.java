package br.com.poc.testmain;

import br.com.poc.pool.GenericPool;
import br.com.poc.pool.GenericPool2;
import br.com.poc.task.GenericTask;
import br.com.poc.task.TaskDelay;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestPool {


    public static void main(String[] args) {

        int maxThreads = 100;
        int qtdTasks = 50;
//        int qtdTasks = 10;
        int delayMiliseconds = 1;

        DateTime startTime = DateTime.now();
        GenericPool pool = new GenericPool(maxThreads, "pool-1");
        for (int i = 0; i < qtdTasks; i++) {
            GenericTask task = new TaskDelay("TASK-" + 100 * i, delayMiliseconds);
            pool.addTask(task);
        }

        while (!pool.allTasksAreComplete()){
            //esperando ser verdadeiro
        }
        imprimeData(startTime);
        pool.stopAllThreads();




        startTime = DateTime.now();
        GenericPool2 pool2 = new GenericPool2(maxThreads, "pool-2");
        for (int i = 0; i < qtdTasks; i++) {
            GenericTask task = new TaskDelay("TASK-" + 100 * i, delayMiliseconds);
            pool2.addTask(task);
        }

        while (!pool2.allTasksAreComplete()){
            //esperando ser verdadeiro
        }
        imprimeData(startTime);
        pool2.stopAllThreads();

    }


    private static void imprimeData(DateTime startTime){
        DateTime endTimeBlock = DateTime.now();
        System.out.println(endTimeBlock.getMillis() - startTime.getMillis());
    }



}
