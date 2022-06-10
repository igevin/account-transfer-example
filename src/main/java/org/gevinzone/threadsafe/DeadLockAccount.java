package org.gevinzone.threadsafe;

import org.gevinzone.Account;

public class DeadLockAccount extends Account {
    public DeadLockAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        synchronized (this.getId()) {
            synchronized (target.getId()) {
                transferAccount(target, amount);
            }
        }
    }
}
