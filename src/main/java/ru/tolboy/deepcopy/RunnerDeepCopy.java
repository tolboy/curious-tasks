package ru.tolboy.deepcopy;

import java.util.concurrent.Callable;

public class RunnerDeepCopy<T> implements Callable<T> {
    private final T sourceObject;

    public RunnerDeepCopy(T sourceObject) {
        this.sourceObject = sourceObject;
    }

    @Override
    public T call() {
        Thread.currentThread().setName(this.getClass().getSimpleName() + "[Thread]");
        System.out.println(Thread.currentThread().getName() + " started");
        T copy = CopyUtils.deepCopy(sourceObject);
        System.out.println(Thread.currentThread().getName() + " finished");
        return copy;
    }
}
