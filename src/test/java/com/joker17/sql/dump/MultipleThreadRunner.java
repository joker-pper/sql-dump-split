package com.joker17.sql.dump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultipleThreadRunner {

    private CountDownLatch countDownLatch;

    private List<Runnable> taskList = new ArrayList<>(32);

    public void addTask(Runnable runnable) {
        this.taskList.add(runnable);
    }

    public void addTask(Runnable runnable, int num) {
        for (int i = 0; i < num; i++) {
            this.taskList.add(runnable);
        }
    }

    public void addTask(Runnable... runnable) {
        this.taskList.addAll(Arrays.asList(runnable));
    }

    public void addTask(List<Runnable> runnableList) {
        this.taskList.addAll(runnableList);
    }

    void doTask(Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
    }

    public void doTask() {
        int size = taskList.size();
        countDownLatch = new CountDownLatch(size);

        for (int i = 0; i < size; i++) {
            doTask(taskList.get(i));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
