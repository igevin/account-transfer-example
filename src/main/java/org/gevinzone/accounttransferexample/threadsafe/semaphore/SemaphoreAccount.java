package org.gevinzone.accounttransferexample.threadsafe.semaphore;

import org.gevinzone.accounttransferexample.Account;

import java.util.concurrent.Semaphore;

public class SemaphoreAccount extends Account {
    private final Semaphore semaphore = new Semaphore(1);
    public SemaphoreAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        if (!(target instanceof SemaphoreAccount)) {
            throw new RuntimeException("target is not OrderedLockAccount instance");
        }
        transferWithSemaphore(target, amount);
    }


    private void transferWithSemaphore(Account target, int amount) {
        SemaphoreAccount left = this.getId() < target.getId() ? this : (SemaphoreAccount)target;
        SemaphoreAccount right = this.getId() < target.getId() ? (SemaphoreAccount)target : this;
        try {
            left.semaphore.acquire();
            try {
                right.semaphore.acquire();
                transferAccount(target, amount);
            } finally {
                right.semaphore.release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            left.semaphore.release();
        }
    }
}
