package org.gevinzone;

@FunctionalInterface
public interface TransferAccount {
    void transfer(Account target, int amount);
}
