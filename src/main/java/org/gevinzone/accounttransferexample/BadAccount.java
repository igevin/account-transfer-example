package org.gevinzone.accounttransferexample;


public class BadAccount extends Account {
    public BadAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        synchronized (this) {
            transferAccount(target, amount);
        }
    }
}
