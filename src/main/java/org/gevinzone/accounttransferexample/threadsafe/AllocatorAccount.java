package org.gevinzone.accounttransferexample.threadsafe;

import org.gevinzone.accounttransferexample.Account;
import org.gevinzone.accounttransferexample.allocator.Allocator;
import org.gevinzone.accounttransferexample.allocator.BasicAllocator;

public class AllocatorAccount extends Account {
    private final Allocator allocator = BasicAllocator.getInstance();
    public AllocatorAccount(long id, int balance) {
        super(id, balance);
    }

    @Override
    public void transfer(Account target, int amount) {
        try {
            allocator.apply(this, target);
            transferAccount(target, amount);
            allocator.free(this, target);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
