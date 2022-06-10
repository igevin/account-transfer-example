package org.gevinzone.accounttransferexample.threadsafe.semaphore;

import org.gevinzone.accounttransferexample.Account;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TrySemaphoreAccount extends Account {
    private final Semaphore semaphore = new Semaphore(1);

    public TrySemaphoreAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        if (!(target instanceof TrySemaphoreAccount)) {
            throw new RuntimeException("target is not instance of TrySemaphoreAccount");
        }
        transferWithSemaphore((TrySemaphoreAccount) target, amount);

    }

    private void transferWithSemaphore(TrySemaphoreAccount target, int amount) {
        while (true) {
            if (semaphore.tryAcquire()) {
                try {
                    if (target.semaphore.tryAcquire()) {
                        try {
                            transferAccount(target, amount);
                            break;
                        } finally {
                            target.semaphore.release();
                        }
                    }
                } finally {
                    semaphore.release();
                }
            }
            // 防止活锁
            randomSleepForLiveLock();
        }
    }

    private void randomSleepForLiveLock() {
        try {
            TimeUnit.NANOSECONDS.sleep((long) (Math.random() * 10));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
