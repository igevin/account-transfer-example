package org.gevinzone.accounttransferexample.threadsafe;

import org.gevinzone.accounttransferexample.Account;

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
