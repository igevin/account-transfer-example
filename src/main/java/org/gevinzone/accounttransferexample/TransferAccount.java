package org.gevinzone.accounttransferexample;

@FunctionalInterface
public interface TransferAccount {
    void transfer(Account target, int amount);
}
