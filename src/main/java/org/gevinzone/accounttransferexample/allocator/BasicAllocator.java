package org.gevinzone.accounttransferexample.allocator;

import java.util.HashSet;

public class BasicAllocator implements Allocator {
    private final HashSet<Object> container;

    private BasicAllocator() {
        container = new HashSet<>(1000);
    }

    public static BasicAllocator getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void apply(Object from, Object to) throws InterruptedException {
        synchronized (this) {
            while (container.contains(from) || container.contains(to)) {
                wait();
            }
            container.add(from);
            container.add(to);
        }
    }

    @Override
    public void free(Object from, Object to) {
        synchronized (this) {
            container.remove(from);
            container.remove(to);
            notifyAll();
        }
    }

    private static class InstanceHolder {
        private static final BasicAllocator instance = new BasicAllocator();
    }
}
