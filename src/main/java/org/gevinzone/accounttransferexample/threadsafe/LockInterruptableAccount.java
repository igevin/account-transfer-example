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

    private void transferWithLockInterruptable(LockInterruptableAccount target, int amount) {
        try {
            lock.lockInterruptibly();
            randomSleep();
            try {
                target.lock.lockInterruptibly();
                transferAccount(target, amount);
            } finally {
                target.lock.unlock();
            }
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlock();
        }
    }

    private void transferWithAutoLockInterruptable(LockInterruptableAccount target, int amount) {
        try (AutoCloseableLock autoLock = new AutoCloseableLock(lock)) {
            autoLock.lockInterruptibly();
            try (AutoCloseableLock autoLock2 = new AutoCloseableLock(target.lock)) {
                autoLock2.lockInterruptibly();
                transferAccount(target, amount);
            }
        } catch (InterruptedException ignored) {

        }
    }

    private void randomSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 10));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
