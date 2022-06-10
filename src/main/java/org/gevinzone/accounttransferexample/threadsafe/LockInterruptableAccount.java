package org.gevinzone.accounttransferexample.threadsafe;

import org.gevinzone.accounttransferexample.Account;
import org.gevinzone.accounttransferexample.AutoCloseableLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockInterruptableAccount extends Account {
    private final Lock lock = new ReentrantLock();

    public LockInterruptableAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        if (!(target instanceof LockInterruptableAccount)) {
            throw new RuntimeException("target is not instance of TryLockAccount");
        }
//        transferWithLockInterruptable((LockInterruptableAccount) target, amount);
        transferWithAutoLockInterruptable((LockInterruptableAccount) target, amount);

    }

    private void transferWithLockInterruptable(LockInterruptableAccount target, int amount)  {
        while (true) {
            try {
                lock.lockInterruptibly();
                if (!target.lock.tryLock()) {
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    transferAccount(target, amount);
                    break;
                } finally {
                    target.lock.unlock();
                }
            } catch (InterruptedException ignored) {

            } finally {
                lock.unlock();
            }

            // 防止活锁
            randomSleepForLiveLock();
        }
    }

    private void transferWithAutoLockInterruptable(LockInterruptableAccount target, int amount) {
        while (true) {
            try (AutoCloseableLock autoLock = new AutoCloseableLock(lock)) {
                autoLock.lockInterruptibly();
                if (!target.lock.tryLock()) {
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    transferAccount(target, amount);
                    break;
                } finally {
                    target.lock.unlock();
                }
            } catch (InterruptedException ignored) {
            }
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
