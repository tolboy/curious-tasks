package ru.tolboy;

import ru.tolboy.deepcopy.RunnerDeepCopy;
import ru.tolboy.deepcopy.testentities.Man;
import ru.tolboy.ipcounter.RunnerIPCounter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

    private static final int THREAD_LIMIT = 2;
    private static final String TASK_SEPARATOR = "***";

    public static void main(String[] args) throws Exception {
        // Task threads container init
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
        // Deep-clone task
        consoleTaskSeparatorRepeater();
        Man originalMan = new Man("Smith", 40, List.of("Book_1", "Book_2", "Book_3"));
        RunnerDeepCopy<Man> deepCopyThread = new RunnerDeepCopy<>(originalMan);
        Man deepCopyResult = executor.submit(deepCopyThread).get();
        System.out.println("Copied object is not the same as original: " + (originalMan != deepCopyResult));
        consoleTaskSeparatorRepeater();
        // IPs unique counter task
        RunnerIPCounter ipCounterThread = new RunnerIPCounter(args);
        Long ipCounterResult = executor.submit(ipCounterThread).get();
        System.out.println("Unique ips must be equal 1 billion: " + ipCounterResult);
        // All tasks are ended -> shutdown to finish this demo program
        executor.shutdown();
    }

    private static void consoleTaskSeparatorRepeater() {
        IntStream.rangeClosed(1, 3).forEach(it -> System.out.println(TASK_SEPARATOR));
    }
}