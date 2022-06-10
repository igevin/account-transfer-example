package org.gevinzone.accounttransferexample;

import org.gevinzone.accounttransferexample.threadsafe.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = createExecutor();

        Account a, b;

//        a = new Account(1, 1000);
//        b = new Account(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new DeadLockAccount(1, 1000);
//        b = new DeadLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new OrderedLockAccount(1, 1000);
//        b = new OrderedLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

        a = new ClassLockAccount(1, 1000);
        b = new ClassLockAccount(2, 1000);
        concurrentAccountTransfer(a, b, executor);


        executor.shutdown();
    }

    private static ExecutorService createExecutor() {
        int core = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(core + 1);
    }

    private static void concurrentAccountTransfer(Account a, Account b, ExecutorService executor) throws InterruptedException {
        int loop = 50;
        CountDownLatch latchA = new CountDownLatch(loop);
        CountDownLatch latchB = new CountDownLatch(loop);
        showAccounts(a, b, "Before transfer: ");

        System.out.println("Start concurrent transfer");

        for (int i = 0; i < loop; i++) {
            executor.execute(() -> {
                a.transfer(b, 1);
                latchA.countDown();
            });

            executor.execute(() -> {
                b.transfer(a, 1);
                latchB.countDown();
            });
        }
        latchA.await();
        latchB.await();

        System.out.println("Concurrent transfer complete");

        showAccounts(a, b, "After transfer: ");
    }

    private static void showAccounts(Account a, Account b, String msg) {
        System.out.println(msg);
        System.out.printf("A: %d, B: %d\n", a.getBalance(), b.getBalance());
    }

}