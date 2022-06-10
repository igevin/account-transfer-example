package org.gevinzone.accounttransferexample.threadsafe;

import org.gevinzone.accounttransferexample.Account;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockAccount extends Account {
    private final Lock lock = new ReentrantLock();

    public TryLockAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        if (!(target instanceof TryLockAccount)) {
            throw new RuntimeException("target is not instance of TryLockAccount");
        }
        transferWithTryLock((TryLockAccount) target, amount);

    }

    private void transferWithTryLock(TryLockAccount target, int amount) {
        while (true) {
            if (lock.tryLock()) {
                try {
                    if (target.lock.tryLock()) {
                        try {
                            transferAccount(target, amount);
                            break;
                        } finally {
                            target.lock.unlock();
                        }
                    }
                } finally {
                    lock.unlock();
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
