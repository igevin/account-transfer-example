package org.gevinzone.accounttransferexample.allocator;

public interface Allocator {
    void apply(Object from, Object to) throws InterruptedException;
    void free(Object from, Object to);
}
