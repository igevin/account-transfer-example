package org.gevinzone.threadsafe;

import org.gevinzone.Account;

public class ClassLockAccount extends Account {
    public ClassLockAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        synchronized (ClassLockAccount.class) {
            transferAccount(target, amount);
        }
    }
}
