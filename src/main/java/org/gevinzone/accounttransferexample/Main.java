package org.gevinzone.accounttransferexample;

import org.gevinzone.accounttransferexample.threadsafe.*;
import org.gevinzone.accounttransferexample.threadsafe.semaphore.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = createExecutor();

//        lockTest(executor);
//        semaphoreTest(executor);
        lockInterruptableTest();

        executor.shutdown();
    }

    private static void lockTest(ExecutorService executor) throws InterruptedException {
        Account a, b;

//        a = new Account(1, 1000);
//        b = new Account(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new BadAccount(1, 1000);
//        b = new BadAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new DeadLockAccount(1, 1000);
//        b = new DeadLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new OrderedLockAccount(1, 1000);
//        b = new OrderedLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new ClassLockAccount(1, 1000);
//        b = new ClassLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

//        a = new TryLockAccount(1, 1000);
//        b = new TryLockAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);


        a = new AllocatorAccount(1, 1000);
        b = new AllocatorAccount(2, 1000);
        concurrentAccountTransfer(a, b, executor);
    }

    private static void semaphoreTest(ExecutorService executor) throws InterruptedException {
        Account a, b;

//        a = new SemaphoreAccount(1, 1000);
//        b = new SemaphoreAccount(2, 1000);
//        concurrentAccountTransfer(a, b, executor);

        a = new TrySemaphoreAccount(1, 1000);
        b = new TrySemaphoreAccount(2, 1000);
        concurrentAccountTransfer(a, b, executor);
    }

    private static ExecutorService createExecutor() {
        int core = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(core + 1);
    }

    private static void concurrentAccountTransfer(Account a, Account b, ExecutorService executor) throws InterruptedException {
        int loop = 5000;
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

    private static void lockInterruptableTest() throws InterruptedException {
        LockInterruptableAccount a = new LockInterruptableAccount(1, 1000);
        LockInterruptableAccount b = new LockInterruptableAccount(2, 1000);
        System.out.println("before...");
        System.out.printf("A: %d, B: %d\n", a.getBalance(), b.getBalance());

        BiConsumer<LockInterruptableAccount, LockInterruptableAccount> consumer =
                (o1, o2) -> o1.transfer(o2, 1);

        Thread thread1 = new Thread(() -> consumer.accept(a, b));
        Thread thread2 = new Thread(() -> consumer.accept(b, a));
        thread1.start();
        thread2.start();
        thread1.interrupt();
        thread1.join();
        thread2.join();
        System.out.println("after...");
        System.out.printf("A: %d, B: %d\n", a.getBalance(), b.getBalance());
    }

}