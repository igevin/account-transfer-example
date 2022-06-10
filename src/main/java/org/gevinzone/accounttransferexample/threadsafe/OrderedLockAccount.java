package org.gevinzone.accounttransferexample.threadsafe;

import org.gevinzone.accounttransferexample.Account;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderedLockAccount extends Account {
    private final Lock lock = new ReentrantLock();
    public OrderedLockAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        if (!(target instanceof OrderedLockAccount)) {
            throw new RuntimeException("target is not OrderedLockAccount instance");
        }
//        transferWithLock(target, amount);
        transferWithSynchronized(target, amount);
    }

    private void transferWithSynchronized(Account target, int amount) {
        OrderedLockAccount left = this.getId() < target.getId() ? this : (OrderedLockAccount)target;
        OrderedLockAccount right = this.getId() < target.getId() ? (OrderedLockAccount)target : this;
        synchronized (left.getId()) {
            synchronized (right.getId()) {
                transferAccount(target, amount);
            }
        }
    }

    private void transferWithLock(Account target, int amount) {
        OrderedLockAccount left = this.getId() < target.getId() ? this : (OrderedLockAccount)target;
        OrderedLockAccount right = this.getId() < target.getId() ? (OrderedLockAccount)target : this;
        try {
            left.lock.lock();
            try {
                right.lock.lock();
                transferAccount(target, amount);
            } finally {
                right.lock.unlock();
            }
        } finally {
            left.lock.unlock();
        }
    }
}
