package br.com.poc;

import lombok.Builder;
import lombok.Data;

import java.text.MessageFormat;

@Data
@Builder
public class LogSaida {

    private final String POOL_COUNTER = "| thread name: {0} | counter: {1} | THREAD ";
    private final String POOL_COUNTER_ATOMIC = "| thread name: {0} | counter: {1} | THREAD ATOMIC";

    private Integer counter;
    private String threadName;

    private boolean atomic=false;


    public void printAll(){
        if (isAtomic()){
            System.out.println(MessageFormat.format(POOL_COUNTER_ATOMIC, threadName,counter));
        }else{
            System.out.println(MessageFormat.format(POOL_COUNTER, threadName,counter));
        }
    }

}
