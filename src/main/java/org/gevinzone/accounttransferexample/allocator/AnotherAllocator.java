package org.gevinzone.accounttransferexample.allocator;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AnotherAllocator implements Allocator {
    private final HashSet<Object> container;
    private final Lock lock = new ReentrantLock();
    private final Condition objectOccupied = lock.newCondition();

    private AnotherAllocator() {
        container = new HashSet<>(1000);
    }

    public static AnotherAllocator getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void apply(Object from, Object to) throws InterruptedException {
        try {
            lock.lock();
            while (container.contains(from) || container.contains(to)) {
                objectOccupied.await();
            }
            container.add(from);
            container.add(to);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void free(Object from, Object to) {
        try {
            lock.lock();
            container.remove(from);
            container.remove(to);
            objectOccupied.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private static class InstanceHolder {
        private static final AnotherAllocator instance = new AnotherAllocator();
    }
}
