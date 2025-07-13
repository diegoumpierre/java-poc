# java_locks_syncronized_atomic_variables

Thanks for visiting The Generic Pool Project!

This is a custom pool for your tasks.

### Features:
- create a multiple's pool threads
- set a timeout (in minutes) for a poll without task, default timeout for a pool is one minute without task
- Stop all threads - you can finalize your pool
- Resize the pool - you can resize the pool in runtime
- If the task throws an Exception, the pool print in the console and go to next task 


### How to use
1. clone de project
2. in the main directory execute
> mvn clean install
3. import in your project:
```
<dependency>
    <groupId>br.com.poc</groupId> 
    <artifactId>java_locks_syncronized_atomic_variables</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
4. Create your own class TASK and implement GenericTask
```
...
public class MyTaskExample implements GenericTask {

    @Override
    public void run(String threadName) {
        System.out.println("do something");
    }
    
    @Override
    public String getTaskName() {
        return "generic name";
    }
...
}
```
5. Create your GenericPool and add your TASKS (MyTaskExample)
```
...
GenericPool pool = new GenericPool(2,"pool-1");
GenericTask task = new MyTaskExample();
pool.addTask(task);
...
```
6. The pool was created and start to execute, the pool will stop when all task are executed and the timeout is attended.

### Pool methods
| Syntax | Description                                                                                                                   |
| ----------- |-------------------------------------------------------------------------------------------------------------------------------|
| giveMeTask | Used by the THREADS - to get a task for execution.                                                                            |
| stoppingThread | Used by the THREADS - manage the quantity THREADS execution                                                                   |
| addTask | Add TASK in the pool                                                                                                          |
| resizePool | You can resize the pool in runtime                                                                                            |
| stopAllThreads | STOP all threads in the pool                                                                                                  |
| setPoolTimeOutMin | Case the pool stay without TASK to execute, they will stop automaticly after the timeout in minutes. The default are 1 minute |
| getPoolName | Get the name of the pool                                                                                                      |

### ERROR printed if TASK exception
```("TASK ERROR !!! | POOL - {0} | TASK = {1} | ERROR: {2}";) ```